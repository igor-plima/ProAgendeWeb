package br.com.proagende.util;

import br.com.proagende.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** CT-08: conversão de data e hora digitadas. */
class DataHoraUtilTest {

    @Test
    void converteTextoParaLocalDateTime() {
        assertEquals(LocalDateTime.of(2030, 1, 15, 10, 0), DataHoraUtil.converter("15/01/2030", "10:00"));
    }

    @Test
    void ignoraEspacosNasPontas() {
        assertEquals(LocalDateTime.of(2030, 1, 15, 9, 5), DataHoraUtil.converter(" 15/01/2030 ", " 09:05 "));
    }

    @ParameterizedTest(name = "{0} {1} é inválido")
    @CsvSource({"31/02/2030,10:00", "15/13/2030,10:00", "2030-01-15,10:00", "15/01/2030,25:00", "15/01/2030,10:60", "15/01/2030,abc"})
    void rejeitaDataOuHoraInvalida(String data, String hora) {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> DataHoraUtil.converter(data, hora));
        assertEquals("Data ou hora inválida. Use dd/MM/aaaa e HH:mm.", e.getMessage());
    }

    @Test
    void rejeitaNulo() {
        assertThrows(RegraNegocioException.class, () -> DataHoraUtil.converter(null, null));
    }

    @Test
    void formataNoPadraoBrasileiro() {
        assertEquals("15/01/2030 10:00", DataHoraUtil.formatar(LocalDateTime.of(2030, 1, 15, 10, 0)));
    }
}
