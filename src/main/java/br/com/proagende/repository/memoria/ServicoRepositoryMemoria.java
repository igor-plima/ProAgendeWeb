package br.com.proagende.repository.memoria;

import br.com.proagende.model.Servico;
import br.com.proagende.repository.ServicoRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ServicoRepositoryMemoria extends RepositorioMemoria<Servico> implements ServicoRepository {

    @Override
    protected int obterId(Servico s) { return s.getIdServico(); }

    @Override
    protected void definirId(Servico s, int id) { s.setIdServico(id); }

    @Override
    public List<Servico> listarPorProfissional(int idProfissional) {
        return stream().filter(s -> s.getIdProfissional() == idProfissional).collect(Collectors.toList());
    }
}
