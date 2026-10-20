package br.com.proagende.repository.jdbc;

import br.com.proagende.config.FabricaConexao;
import br.com.proagende.model.Cliente;
import br.com.proagende.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

public class ClienteRepositoryJdbc implements ClienteRepository {

    private static final String INSERIR =
            "INSERT INTO Cliente (nome, email, telefone) VALUES (?, ?, ?)";
    private static final String BUSCAR =
            "SELECT id_Cliente, nome, email, telefone FROM Cliente WHERE id_Cliente = ?";
    private static final String LISTAR =
            "SELECT id_Cliente, nome, email, telefone FROM Cliente ORDER BY nome";
    private static final String ATUALIZAR =
            "UPDATE Cliente SET nome = ?, email = ?, telefone = ? WHERE id_Cliente = ?";
    private static final String EXCLUIR =
            "DELETE FROM Cliente WHERE id_Cliente = ?";

    private final ExecutorJdbc executor;

    public ClienteRepositoryJdbc(FabricaConexao fabrica) {
        this.executor = new ExecutorJdbc(fabrica);
    }

    @Override
    public Cliente salvar(Cliente c) {
        int id = executor.inserir(INSERIR, stmt -> {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getTelefone());
        });
        c.setIdCliente(id);
        return c;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return executor.consultar(BUSCAR, stmt -> stmt.setInt(1, id), rs -> new Cliente(
                rs.getInt("id_Cliente"), rs.getString("nome"),
                rs.getString("email"), rs.getString("telefone"))).stream().findFirst();
    }

    @Override
    public List<Cliente> listarTodos() {
        return executor.consultar(LISTAR, ExecutorJdbc.SEM_PARAMETROS, rs -> new Cliente(
                rs.getInt("id_Cliente"), rs.getString("nome"),
                rs.getString("email"), rs.getString("telefone")));
    }

    @Override
    public void atualizar(Cliente c) {
        executor.atualizar(ATUALIZAR, stmt -> {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getTelefone());
            stmt.setInt(4, c.getIdCliente());
        });
    }

    @Override
    public void excluir(int id) {
        executor.atualizar(EXCLUIR, stmt -> stmt.setInt(1, id));
    }
}
