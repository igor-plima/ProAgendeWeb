package br.com.proagende.repository.memoria;

import br.com.proagende.model.Cliente;
import br.com.proagende.repository.ClienteRepository;

public class ClienteRepositoryMemoria extends RepositorioMemoria<Cliente> implements ClienteRepository {

    @Override
    protected int obterId(Cliente c) { return c.getIdCliente(); }

    @Override
    protected void definirId(Cliente c, int id) { c.setIdCliente(id); }
}
