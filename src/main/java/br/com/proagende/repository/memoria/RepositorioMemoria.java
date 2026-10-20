package br.com.proagende.repository.memoria;

import br.com.proagende.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Base para repositórios em memória. Permite executar os testes sem MySQL
 * e demonstra que os services dependem da interface, não do JDBC.
 */
public abstract class RepositorioMemoria<T> implements Repository<T> {

    private final Map<Integer, T> dados = new LinkedHashMap<>();
    private final AtomicInteger sequencia = new AtomicInteger(0);

    protected abstract int obterId(T entidade);

    protected abstract void definirId(T entidade, int id);

    @Override
    public T salvar(T entidade) {
        int id = sequencia.incrementAndGet();
        definirId(entidade, id);
        dados.put(id, entidade);
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(int id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void atualizar(T entidade) {
        dados.put(obterId(entidade), entidade);
    }

    @Override
    public void excluir(int id) {
        dados.remove(id);
    }

    protected Stream<T> stream() {
        return dados.values().stream();
    }
}
