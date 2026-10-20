package br.com.proagende.service;

import br.com.proagende.exception.RegraNegocioException;
import br.com.proagende.model.Profissional;
import br.com.proagende.repository.ProfissionalRepository;
import br.com.proagende.repository.ServicoRepository;
import br.com.proagende.util.Validador;

import java.util.List;

/** Regras de negócio de profissionais. Não conhece Swing nem JDBC. */
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository, ServicoRepository servicoRepository) {
        this.profissionalRepository = profissionalRepository;
        this.servicoRepository = servicoRepository;
    }

    public Profissional cadastrar(String nome, String email, String telefone) {
        Profissional profissional = new Profissional();
        preencher(profissional, nome, email, telefone);
        return profissionalRepository.salvar(profissional);
    }

    public Profissional atualizar(int id, String nome, String email, String telefone) {
        Profissional profissional = buscar(id);
        preencher(profissional, nome, email, telefone);
        profissionalRepository.atualizar(profissional);
        return profissional;
    }

    public Profissional buscar(int id) {
        return profissionalRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Profissional não encontrado (id " + id + ")."));
    }

    public List<Profissional> listar() {
        return profissionalRepository.listarTodos();
    }

    public void excluir(int id) {
        buscar(id);
        if (!servicoRepository.listarPorProfissional(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir: o profissional possui serviços cadastrados.");
        }
        profissionalRepository.excluir(id);
    }

    private void preencher(Profissional profissional, String nome, String email, String telefone) {
        profissional.setNome(Validador.exigirTexto(nome, "Nome", 100));
        profissional.setEmail(Validador.exigirEmail(email));
        profissional.setTelefone(Validador.exigirTelefone(telefone));
    }
}
