package br.com.proagende.util;

import br.com.proagende.exception.RegraNegocioException;

import java.util.regex.Pattern;

/**
 * Regras de validação reutilizáveis. Antes estavam espalhadas dentro das telas Swing
 * (ex.: matches("\\d+") em CadastroClientes); agora servem a qualquer interface.
 */
public final class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEFONE = Pattern.compile("^\\d{10,11}$");

    private Validador() {
    }

    /** Exige texto não vazio e dentro do limite da coluna; devolve o texto sem espaços nas pontas. */
    public static String exigirTexto(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("O campo " + campo + " é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new RegraNegocioException("O campo " + campo + " aceita no máximo " + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }

    public static String exigirEmail(String valor) {
        String email = exigirTexto(valor, "E-mail", 100);
        if (!EMAIL.matcher(email).matches()) {
            throw new RegraNegocioException("E-mail inválido: " + email);
        }
        return email;
    }

    /** Telefone com DDD, somente dígitos (10 ou 11). */
    public static String exigirTelefone(String valor) {
        String telefone = exigirTexto(valor, "Telefone", 20);
        if (!TELEFONE.matcher(telefone).matches()) {
            throw new RegraNegocioException("Telefone inválido: informe DDD + número, somente dígitos (10 ou 11).");
        }
        return telefone;
    }

    public static double exigirPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new RegraNegocioException("O campo " + campo + " deve ser maior que zero.");
        }
        return valor;
    }

    public static int exigirPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new RegraNegocioException("O campo " + campo + " deve ser maior que zero.");
        }
        return valor;
    }
}
