package br.com.proagende.config;

import br.com.proagende.exception.PersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementação de {@link FabricaConexao} para MySQL via JDBC.
 * URL, usuário e senha não ficam mais no código: vêm de variáveis de ambiente
 * (PROAGENDE_DB_URL, PROAGENDE_DB_USER, PROAGENDE_DB_PASSWORD) ou do arquivo banco.properties.
 */
public class ConexaoBanco implements FabricaConexao {

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoBanco(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    /** Monta a configuração a partir do ambiente; variáveis de ambiente têm prioridade sobre o arquivo. */
    public static ConexaoBanco aPartirDoAmbiente() {
        Properties props = new Properties();
        try (InputStream in = ConexaoBanco.class.getResourceAsStream("/banco.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo banco.properties.", e);
        }

        String url = valor("PROAGENDE_DB_URL", props, "db.url");
        String usuario = valor("PROAGENDE_DB_USER", props, "db.user");
        String senha = valor("PROAGENDE_DB_PASSWORD", props, "db.password");

        if (url == null || usuario == null) {
            throw new PersistenciaException("Configuração do banco ausente. Crie src/main/resources/banco.properties "
                    + "(modelo: banco.properties.example) ou defina as variáveis de ambiente PROAGENDE_DB_*.");
        }
        return new ConexaoBanco(url, usuario, senha == null ? "" : senha);
    }

    private static String valor(String variavelAmbiente, Properties props, String chave) {
        String doAmbiente = System.getenv(variavelAmbiente);
        if (doAmbiente != null && !doAmbiente.isBlank()) {
            return doAmbiente;
        }
        return props.getProperty(chave);
    }

    @Override
    public Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
