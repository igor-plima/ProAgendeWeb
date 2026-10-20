package br.com.proagende.repository.jdbc;

import br.com.proagende.config.FabricaConexao;
import br.com.proagende.model.Profissional;
import br.com.proagende.repository.ProfissionalRepository;

import java.util.List;
import java.util.Optional;

public class ProfissionalRepositoryJdbc implements ProfissionalRepository {

    private static final String INSERIR =
            "INSERT INTO Profissional (nome, email, telefone) VALUES (?, ?, ?)";
    private static final String BUSCAR =
            "SELECT id_Profissional, nome, email, telefone FROM Profissional WHERE id_Profissional = ?";
    private static final String LISTAR =
            "SELECT id_Profissional, nome, email, telefone FROM Profissional ORDER BY nome";
    private static final String ATUALIZAR =
            "UPDATE Profissional SET nome = ?, email = ?, telefone = ? WHERE id_Profissional = ?";
    private static final String EXCLUIR =
            "DELETE FROM Profissional WHERE id_Profissional = ?";

    private final ExecutorJdbc executor;

    public ProfissionalRepositoryJdbc(FabricaConexao fabrica) {
        this.executor = new ExecutorJdbc(fabrica);
    }

    @Override
    public Profissional salvar(Profissional p) {
        int id = executor.inserir(INSERIR, stmt -> {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getEmail());
            stmt.setString(3, p.getTelefone());
        });
        p.setIdProfissional(id);
        return p;
    }

    @Override
    public Optional<Profissional> buscarPorId(int id) {
        return executor.consultar(BUSCAR, stmt -> stmt.setInt(1, id), rs -> new Profissional(
                rs.getInt("id_Profissional"), rs.getString("nome"),
                rs.getString("email"), rs.getString("telefone"))).stream().findFirst();
    }

    @Override
    public List<Profissional> listarTodos() {
        return executor.consultar(LISTAR, ExecutorJdbc.SEM_PARAMETROS, rs -> new Profissional(
                rs.getInt("id_Profissional"), rs.getString("nome"),
                rs.getString("email"), rs.getString("telefone")));
    }

    @Override
    public void atualizar(Profissional p) {
        executor.atualizar(ATUALIZAR, stmt -> {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getEmail());
            stmt.setString(3, p.getTelefone());
            stmt.setInt(4, p.getIdProfissional());
        });
    }

    @Override
    public void excluir(int id) {
        executor.atualizar(EXCLUIR, stmt -> stmt.setInt(1, id));
    }
}
