package br.com.proagende.repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência (CRUD) independente de tecnologia.
 * Os services conhecem apenas estas interfaces; JDBC ou memória são detalhes de implementação.
 */
public interface Repository<T> {

    /** Grava a entidade e devolve a mesma instância já com o id gerado. */
    T salvar(T entidade);

    Optional<T> buscarPorId(int id);

    List<T> listarTodos();

    void atualizar(T entidade);

    void excluir(int id);
}
