package br.com.proagende.service;

import br.com.proagende.exception.RegraNegocioException;
import br.com.proagende.model.Cliente;
import br.com.proagende.repository.AgendamentoRepository;
import br.com.proagende.repository.ClienteRepository;
import br.com.proagende.util.Validador;

import java.util.List;

/** Regras de negócio de clientes. A validação que ficava em CadastroClientes (Swing) agora mora aqui. */
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AgendamentoRepository agendamentoRepository;

    public ClienteService(ClienteRepository clienteRepository, AgendamentoRepository agendamentoRepository) {
        this.clienteRepository = clienteRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public Cliente cadastrar(String nome, String email, String telefone) {
        Cliente cliente = new Cliente();
        preencher(cliente, nome, email, telefone);
        return clienteRepository.salvar(cliente);
    }

    public Cliente atualizar(int id, String nome, String email, String telefone) {
        Cliente cliente = buscar(id);
        preencher(cliente, nome, email, telefone);
        clienteRepository.atualizar(cliente);
        return cliente;
    }

    public Cliente buscar(int id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado (id " + id + ")."));
    }

    public List<Cliente> listar() {
        return clienteRepository.listarTodos();
    }

    public void excluir(int id) {
        buscar(id);
        if (!agendamentoRepository.listarPorCliente(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir: o cliente possui agendamentos.");
        }
        clienteRepository.excluir(id);
    }

    private void preencher(Cliente cliente, String nome, String email, String telefone) {
        cliente.setNome(Validador.exigirTexto(nome, "Nome", 100));
        cliente.setEmail(Validador.exigirEmail(email));
        cliente.setTelefone(Validador.exigirTelefone(telefone));
    }
}
