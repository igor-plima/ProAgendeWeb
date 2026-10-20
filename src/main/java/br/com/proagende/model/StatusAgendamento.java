package br.com.proagende.model;

/**
 * Status possíveis de um agendamento.
 * Substitui as "strings mágicas" do projeto desktop e espelha o ENUM da tabela Agendamento.
 */
public enum StatusAgendamento {

    PENDENTE("pendente", "Pendente"),
    CONFIRMADO("confirmado", "Confirmado"),
    CANCELADO("cancelado", "Cancelado");

    private final String valorBanco;
    private final String rotulo;

    StatusAgendamento(String valorBanco, String rotulo) {
        this.valorBanco = valorBanco;
        this.rotulo = rotulo;
    }

    /** Valor gravado na coluna ENUM do MySQL. */
    public String getValorBanco() {
        return valorBanco;
    }

    /** Texto amigável para exibir em telas (combos, listas). */
    public String getRotulo() {
        return rotulo;
    }

    /** Converte o valor lido do banco para o enum. Valor nulo equivale ao padrão da tabela (pendente). */
    public static StatusAgendamento deValorBanco(String valor) {
        if (valor == null) {
            return PENDENTE;
        }
        for (StatusAgendamento status : values()) {
            if (status.valorBanco.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status desconhecido: " + valor);
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
