package br.com.proagende.repository.memoria;

import br.com.proagende.model.Agendamento;
import br.com.proagende.model.Servico;
import br.com.proagende.repository.AgendamentoRepository;
import br.com.proagende.repository.ServicoRepository;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AgendamentoRepositoryMemoria extends RepositorioMemoria<Agendamento> implements AgendamentoRepository {

    private final ServicoRepository servicoRepository;

    /** Precisa do repositório de serviços para saber qual profissional atende cada agendamento. */
    public AgendamentoRepositoryMemoria(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    protected int obterId(Agendamento a) { return a.getIdAgendamento(); }

    @Override
    protected void definirId(Agendamento a, int id) { a.setIdAgendamento(id); }

    private List<Agendamento> filtrar(Predicate<Agendamento> condicao) {
        return stream().filter(condicao)
                .sorted(Comparator.comparing(Agendamento::getDataHora))
                .collect(Collectors.toList());
    }

    @Override
    public List<Agendamento> listarPorServico(int idServico) {
        return filtrar(a -> a.getIdServico() == idServico);
    }

    @Override
    public List<Agendamento> listarPorCliente(int idCliente) {
        return filtrar(a -> a.getIdCliente() == idCliente);
    }

    @Override
    public List<Agendamento> listarPorServicoECliente(int idServico, int idCliente) {
        return filtrar(a -> a.getIdServico() == idServico && a.getIdCliente() == idCliente);
    }

    @Override
    public List<Agendamento> listarPorProfissional(int idProfissional) {
        return filtrar(a -> servicoRepository.buscarPorId(a.getIdServico())
                .map(Servico::getIdProfissional)
                .filter(id -> id == idProfissional)
                .isPresent());
    }
}
