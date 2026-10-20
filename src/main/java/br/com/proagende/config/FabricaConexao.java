package br.com.proagende.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstração para obtenção de conexões. Os repositórios dependem desta interface,
 * não de uma classe concreta com credenciais fixas (Princípio da Inversão de Dependência).
 */
public interface FabricaConexao {

    Connection obterConexao() throws SQLException;
}
