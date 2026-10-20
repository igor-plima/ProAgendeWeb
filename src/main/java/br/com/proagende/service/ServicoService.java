package br.com.proagende.service;

import br.com.proagende.exception.RegraNegocioException;
import br.com.proagende.model.Servico;
import br.com.proagende.repository.AgendamentoRepository;
import br.com.proagende.repository.ProfissionalRepository;
import br.com.proagende.repository.ServicoRepository;
import br.com.proagende.util.Validador;

import java.util.List;

/**
 * Regras de negócio de serviços. Aqui o profissional é validado pelo id real
 * (no desktop, um if/else comparava o nome e falhava para profissionais novos).
 */
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AgendamentoRepository agendamentoRepository;

    public ServicoService(ServicoRepository servicoRepository,
                          ProfissionalRepository profissionalRepository,
                          AgendamentoRepository agendamentoRepository) {
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public Servico cadastrar(int idProfissional, String nome, String descricao, double preco, int duracaoMinutos) {
        Servico servico = new Servico();
        preencher(servico, idProfissional, nome, descricao, preco, duracaoMinutos);
        return servicoRepository.salvar(servico);
    }

    public Servico atualizar(int id, int idProfissional, String nome, String descricao,
                             double preco, int duracaoMinutos) {
        Servico servico = buscar(id);
        preencher(servico, idProfissional, nome, descricao, preco, duracaoMinutos);
        servicoRepository.atualizar(servico);
        return servico;
    }

    public Servico buscar(int id) {
        return servicoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado (id " + id + ")."));
    }

    public List<Servico> listar() {
        return servicoRepository.listarTodos();
    }

    public List<Servico> listarPorProfissional(int idProfissional) {
        return servicoRepository.listarPorProfissional(idProfissional);
    }

    public void excluir(int id) {
        buscar(id);
        if (!agendamentoRepository.listarPorServico(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir: o serviço possui agendamentos.");
        }
        servicoRepository.excluir(id);
    }

    private void preencher(Servico servico, int idProfissional, String nome, String descricao,
                           double preco, int duracaoMinutos) {
        if (profissionalRepository.buscarPorId(idProfissional).isEmpty()) {
            throw new RegraNegocioException("Selecione um profissional válido para o serviço.");
        }
        servico.setIdProfissional(idProfissional);
        servico.setNome(Validador.exigirTexto(nome, "Nome", 100));
        servico.setDescricao(Validador.exigirTexto(descricao, "Descrição", 65535));
        servico.setPreco(Validador.exigirPositivo(preco, "Preço"));
        servico.setDuracaoMinutos(Validador.exigirPositivo(duracaoMinutos, "Duração (minutos)"));
    }
}
