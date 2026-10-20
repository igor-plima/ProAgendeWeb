package br.com.proagende.repository.jdbc;

import br.com.proagende.config.FabricaConexao;
import br.com.proagende.model.Agendamento;
import br.com.proagende.model.StatusAgendamento;
import br.com.proagende.repository.AgendamentoRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class AgendamentoRepositoryJdbc implements AgendamentoRepository {

    private static final String COLUNAS =
            "a.id_Agendamento, a.id_Servico, a.id_Cliente, a.data_hora, a.status";

    private static final String INSERIR =
            "INSERT INTO Agendamento (id_Servico, id_Cliente, data_hora, status) VALUES (?, ?, ?, ?)";
    private static final String BUSCAR =
            "SELECT " + COLUNAS + " FROM Agendamento a WHERE a.id_Agendamento = ?";
    private static final String LISTAR =
            "SELECT " + COLUNAS + " FROM Agendamento a ORDER BY a.data_hora";
    private static final String POR_SERVICO =
            "SELECT " + COLUNAS + " FROM Agendamento a WHERE a.id_Servico = ? ORDER BY a.data_hora";
    private static final String POR_CLIENTE =
            "SELECT " + COLUNAS + " FROM Agendamento a WHERE a.id_Cliente = ? ORDER BY a.data_hora";
    private static final String POR_SERVICO_E_CLIENTE =
            "SELECT " + COLUNAS + " FROM Agendamento a WHERE a.id_Servico = ? AND a.id_Cliente = ? "
            + "ORDER BY a.data_hora";
    private static final String POR_PROFISSIONAL =
            "SELECT " + COLUNAS + " FROM Agendamento a "
            + "INNER JOIN Servico s ON s.id_Servico = a.id_Servico "
            + "WHERE s.id_Profissional = ? ORDER BY a.data_hora";
    private static final String ATUALIZAR =
            "UPDATE Agendamento SET id_Servico = ?, id_Cliente = ?, data_hora = ?, status = ? "
            + "WHERE id_Agendamento = ?";
    private static final String EXCLUIR =
            "DELETE FROM Agendamento WHERE id_Agendamento = ?";

    private final ExecutorJdbc executor;

    public AgendamentoRepositoryJdbc(FabricaConexao fabrica) {
        this.executor = new ExecutorJdbc(fabrica);
    }

    private static Agendamento mapear(ResultSet rs) throws SQLException {
        return new Agendamento(rs.getInt("id_Agendamento"), rs.getInt("id_Servico"), rs.getInt("id_Cliente"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                StatusAgendamento.deValorBanco(rs.getString("status")));
    }

    @Override
    public Agendamento salvar(Agendamento a) {
        int id = executor.inserir(INSERIR, stmt -> {
            stmt.setInt(1, a.getIdServico());
            stmt.setInt(2, a.getIdCliente());
            stmt.setTimestamp(3, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(4, a.getStatus().getValorBanco());
        });
        a.setIdAgendamento(id);
        return a;
    }

    @Override
    public Optional<Agendamento> buscarPorId(int id) {
        return executor.consultar(BUSCAR, stmt -> stmt.setInt(1, id), AgendamentoRepositoryJdbc::mapear)
                .stream().findFirst();
    }

    @Override
    public List<Agendamento> listarTodos() {
        return executor.consultar(LISTAR, ExecutorJdbc.SEM_PARAMETROS, AgendamentoRepositoryJdbc::mapear);
    }

    @Override
    public List<Agendamento> listarPorServico(int idServico) {
        return executor.consultar(POR_SERVICO, stmt -> stmt.setInt(1, idServico), AgendamentoRepositoryJdbc::mapear);
    }

    @Override
    public List<Agendamento> listarPorCliente(int idCliente) {
        return executor.consultar(POR_CLIENTE, stmt -> stmt.setInt(1, idCliente), AgendamentoRepositoryJdbc::mapear);
    }

    @Override
    public List<Agendamento> listarPorServicoECliente(int idServico, int idCliente) {
        return executor.consultar(POR_SERVICO_E_CLIENTE, stmt -> {
            stmt.setInt(1, idServico);
            stmt.setInt(2, idCliente);
        }, AgendamentoRepositoryJdbc::mapear);
    }

    @Override
    public List<Agendamento> listarPorProfissional(int idProfissional) {
        return executor.consultar(POR_PROFISSIONAL, stmt -> stmt.setInt(1, idProfissional),
                AgendamentoRepositoryJdbc::mapear);
    }

    @Override
    public void atualizar(Agendamento a) {
        executor.atualizar(ATUALIZAR, stmt -> {
            stmt.setInt(1, a.getIdServico());
            stmt.setInt(2, a.getIdCliente());
            stmt.setTimestamp(3, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(4, a.getStatus().getValorBanco());
            stmt.setInt(5, a.getIdAgendamento());
        });
    }

    @Override
    public void excluir(int id) {
        executor.atualizar(EXCLUIR, stmt -> stmt.setInt(1, id));
    }
}
