package br.com.proagende.repository.jdbc;

import br.com.proagende.config.FabricaConexao;
import br.com.proagende.model.Servico;
import br.com.proagende.repository.ServicoRepository;

import java.util.List;
import java.util.Optional;

public class ServicoRepositoryJdbc implements ServicoRepository {

    private static final String COLUNAS =
            "id_Servico, id_Profissional, nome, descricao, preco, duracao_minutos";

    private static final String INSERIR =
            "INSERT INTO Servico (id_Profissional, nome, descricao, preco, duracao_minutos) VALUES (?, ?, ?, ?, ?)";
    private static final String BUSCAR =
            "SELECT " + COLUNAS + " FROM Servico WHERE id_Servico = ?";
    private static final String LISTAR =
            "SELECT " + COLUNAS + " FROM Servico ORDER BY nome";
    private static final String LISTAR_POR_PROFISSIONAL =
            "SELECT " + COLUNAS + " FROM Servico WHERE id_Profissional = ? ORDER BY nome";
    private static final String ATUALIZAR =
            "UPDATE Servico SET id_Profissional = ?, nome = ?, descricao = ?, preco = ?, duracao_minutos = ? "
            + "WHERE id_Servico = ?";
    private static final String EXCLUIR =
            "DELETE FROM Servico WHERE id_Servico = ?";

    private final ExecutorJdbc executor;

    public ServicoRepositoryJdbc(FabricaConexao fabrica) {
        this.executor = new ExecutorJdbc(fabrica);
    }

    /** Mapeamento único, usado por todas as consultas (antes havia um por método). */
    private static Servico mapear(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Servico(rs.getInt("id_Servico"), rs.getInt("id_Profissional"),
                rs.getString("nome"), rs.getString("descricao"),
                rs.getDouble("preco"), rs.getInt("duracao_minutos"));
    }

    @Override
    public Servico salvar(Servico s) {
        int id = executor.inserir(INSERIR, stmt -> {
            stmt.setInt(1, s.getIdProfissional());
            stmt.setString(2, s.getNome());
            stmt.setString(3, s.getDescricao());
            stmt.setDouble(4, s.getPreco());
            stmt.setInt(5, s.getDuracaoMinutos());
        });
        s.setIdServico(id);
        return s;
    }

    @Override
    public Optional<Servico> buscarPorId(int id) {
        return executor.consultar(BUSCAR, stmt -> stmt.setInt(1, id), ServicoRepositoryJdbc::mapear)
                .stream().findFirst();
    }

    @Override
    public List<Servico> listarTodos() {
        return executor.consultar(LISTAR, ExecutorJdbc.SEM_PARAMETROS, ServicoRepositoryJdbc::mapear);
    }

    @Override
    public List<Servico> listarPorProfissional(int idProfissional) {
        return executor.consultar(LISTAR_POR_PROFISSIONAL, stmt -> stmt.setInt(1, idProfissional),
                ServicoRepositoryJdbc::mapear);
    }

    @Override
    public void atualizar(Servico s) {
        executor.atualizar(ATUALIZAR, stmt -> {
            stmt.setInt(1, s.getIdProfissional());
            stmt.setString(2, s.getNome());
            stmt.setString(3, s.getDescricao());
            stmt.setDouble(4, s.getPreco());
            stmt.setInt(5, s.getDuracaoMinutos());
            stmt.setInt(6, s.getIdServico());
        });
    }

    @Override
    public void excluir(int id) {
        executor.atualizar(EXCLUIR, stmt -> stmt.setInt(1, id));
    }
}
