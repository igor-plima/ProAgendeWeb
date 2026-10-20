package br.com.proagende.repository.jdbc;

import br.com.proagende.config.FabricaConexao;
import br.com.proagende.exception.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Concentra o código repetitivo de JDBC (abrir conexão, preparar comando, fechar recursos,
 * traduzir SQLException). Nos DAOs originais esse bloco era copiado em cada método.
 * Os repositórios passam apenas o SQL, os parâmetros e o mapeamento das linhas.
 */
public class ExecutorJdbc {

    /** Preenche os parâmetros (?) do comando. */
    @FunctionalInterface
    public interface Preenchedor {
        void preencher(PreparedStatement stmt) throws SQLException;
    }

    /** Converte a linha atual do ResultSet em objeto. */
    @FunctionalInterface
    public interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    public static final Preenchedor SEM_PARAMETROS = stmt -> { };

    private final FabricaConexao fabrica;

    public ExecutorJdbc(FabricaConexao fabrica) {
        this.fabrica = fabrica;
    }

    /** Executa um INSERT e devolve o id gerado pelo banco. */
    public int inserir(String sql, Preenchedor preenchedor) {
        try (Connection conn = fabrica.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preenchedor.preencher(stmt);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir no banco: " + e.getMessage(), e);
        }
    }

    /** Executa UPDATE ou DELETE e devolve a quantidade de linhas afetadas. */
    public int atualizar(String sql, Preenchedor preenchedor) {
        try (Connection conn = fabrica.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preenchedor.preencher(stmt);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar o banco: " + e.getMessage(), e);
        }
    }

    /** Executa um SELECT e converte cada linha com o mapeador informado. */
    public <T> List<T> consultar(String sql, Preenchedor preenchedor, Mapeador<T> mapeador) {
        try (Connection conn = fabrica.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preenchedor.preencher(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao consultar o banco: " + e.getMessage(), e);
        }
    }
}
