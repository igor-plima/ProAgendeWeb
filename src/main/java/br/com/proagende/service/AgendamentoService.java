package br.com.proagende.service;

import br.com.proagende.exception.RegraNegocioException;
import br.com.proagende.model.Agendamento;
import br.com.proagende.model.Cliente;
import br.com.proagende.model.Servico;
import br.com.proagende.model.StatusAgendamento;
import br.com.proagende.repository.AgendamentoRepository;
import br.com.proagende.repository.ClienteRepository;
import br.com.proagende.repository.ServicoRepository;
import br.com.proagende.util.DataHoraUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Regras de negócio de agendamentos: validação, status padrão e controle de conflito de horário
 * (um profissional não pode ter dois atendimentos sobrepostos, exceto se o anterior estiver cancelado).
 */
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ServicoRepository servicoRepository,
                              ClienteRepository clienteRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
    }

    /** Variante para interfaces que enviam data e hora como texto (dd/MM/aaaa e HH:mm). */
    public Agendamento agendar(int idServico, int idCliente, String data, String hora, StatusAgendamento status) {
        return agendar(idServico, idCliente, DataHoraUtil.converter(data, hora), status);
    }

    public Agendamento agendar(int idServico, int idCliente, LocalDateTime dataHora, StatusAgendamento status) {
        if (dataHora == null) {
            throw new RegraNegocioException("Informe a data e a hora do agendamento.");
        }
        StatusAgendamento statusFinal = status == null ? StatusAgendamento.PENDENTE : status;
        Servico servico = buscarServico(idServico);
        buscarCliente(idCliente);

        if (statusFinal != StatusAgendamento.CANCELADO) {
            verificarConflito(servico, dataHora, 0);
        }
        return agendamentoRepository.salvar(new Agendamento(0, idServico, idCliente, dataHora, statusFinal));
    }

    public Agendamento buscar(int id) {
        return agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Agendamento não encontrado (id " + id + ")."));
    }

    public List<Agendamento> listar() {
        return agendamentoRepository.listarTodos();
    }

    public List<Agendamento> listarPorServico(int idServico) {
        return agendamentoRepository.listarPorServico(idServico);
    }

    public List<Agendamento> listarPorServicoECliente(int idServico, int idCliente) {
        return agendamentoRepository.listarPorServicoECliente(idServico, idCliente);
    }

    /** Clientes (sem repetição, em ordem alfabética) que possuem agendamento no serviço informado. */
    public List<Cliente> listarClientesPorServico(int idServico) {
        return agendamentoRepository.listarPorServico(idServico).stream()
                .map(Agendamento::getIdCliente)
                .distinct()
                .map(clienteRepository::buscarPorId)
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(Cliente::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Agendamento alterarStatus(int id, StatusAgendamento novoStatus) {
        Objects.requireNonNull(novoStatus, "O novo status é obrigatório.");
        Agendamento agendamento = buscar(id);
        if (novoStatus != StatusAgendamento.CANCELADO) {
            // Reativar um agendamento cancelado exige que o horário ainda esteja livre.
            verificarConflito(buscarServico(agendamento.getIdServico()), agendamento.getDataHora(), id);
        }
        agendamento.setStatus(novoStatus);
        agendamentoRepository.atualizar(agendamento);
        return agendamento;
    }

    public Agendamento cancelar(int id) {
        return alterarStatus(id, StatusAgendamento.CANCELADO);
    }

    public Agendamento reagendar(int id, LocalDateTime novaDataHora) {
        if (novaDataHora == null) {
            throw new RegraNegocioException("Informe a nova data e hora.");
        }
        Agendamento agendamento = buscar(id);
        if (agendamento.getStatus() != StatusAgendamento.CANCELADO) {
            verificarConflito(buscarServico(agendamento.getIdServico()), novaDataHora, id);
        }
        agendamento.setDataHora(novaDataHora);
        agendamentoRepository.atualizar(agendamento);
        return agendamento;
    }

    public void excluir(int id) {
        buscar(id);
        agendamentoRepository.excluir(id);
    }

    // ---------------------------------------------------------------- apoio

    private Servico buscarServico(int idServico) {
        return servicoRepository.buscarPorId(idServico)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado (id " + idServico + ")."));
    }

    private Cliente buscarCliente(int idCliente) {
        return clienteRepository.buscarPorId(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado (id " + idCliente + ")."));
    }

    /** Lança exceção se o intervalo [inicio, inicio + duração] se sobrepõe a outro agendamento ativo do profissional. */
    private void verificarConflito(Servico servico, LocalDateTime inicio, int idIgnorado) {
        LocalDateTime fim = inicio.plusMinutes(duracaoEfetiva(servico));
        Map<Integer, Servico> cacheServicos = new HashMap<>();

        for (Agendamento existente : agendamentoRepository.listarPorProfissional(servico.getIdProfissional())) {
            if (existente.getIdAgendamento() == idIgnorado || existente.getStatus() == StatusAgendamento.CANCELADO) {
                continue;
            }
            Servico servicoExistente = cacheServicos.computeIfAbsent(existente.getIdServico(),
                    id -> servicoRepository.buscarPorId(id).orElse(null));
            LocalDateTime inicioExistente = existente.getDataHora();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoEfetiva(servicoExistente));

            if (inicio.isBefore(fimExistente) && inicioExistente.isBefore(fim)) {
                throw new RegraNegocioException("Conflito de horário: o profissional já possui o agendamento "
                        + existente.getIdAgendamento() + " em " + DataHoraUtil.formatar(inicioExistente) + ".");
            }
        }
    }

    /** Serviço sem duração cadastrada ocupa ao menos 1 minuto, para que dois no mesmo instante conflitem. */
    private int duracaoEfetiva(Servico servico) {
        return (servico == null || servico.getDuracaoMinutos() <= 0) ? 1 : servico.getDuracaoMinutos();
    }
}
