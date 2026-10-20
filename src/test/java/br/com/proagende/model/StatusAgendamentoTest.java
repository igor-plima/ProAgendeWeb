package br.com.proagende.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** CT-09: correspondência entre o enum e o ENUM do MySQL. */
class StatusAgendamentoTest {

    @Test
    void convertePeloValorDoBanco() {
        assertEquals(StatusAgendamento.CONFIRMADO, StatusAgendamento.deValorBanco("confirmado"));
        assertEquals(StatusAgendamento.CANCELADO, StatusAgendamento.deValorBanco("CANCELADO"));
    }

    @Test
    void valorNuloEquivaleAoPadraoDaTabela() {
        assertEquals(StatusAgendamento.PENDENTE, StatusAgendamento.deValorBanco(null));
    }

    @Test
    void valorDesconhecidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> StatusAgendamento.deValorBanco("remarcado"));
    }

    @Test
    void valorGravadoNoBancoEhMinusculo() {
        for (StatusAgendamento status : StatusAgendamento.values()) {
            assertEquals(status.name().toLowerCase(), status.getValorBanco());
        }
    }
}
