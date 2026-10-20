package br.com.proagende.repository;

import br.com.proagende.model.Servico;

import java.util.List;

public interface ServicoRepository extends Repository<Servico> {

    List<Servico> listarPorProfissional(int idProfissional);
}
