package br.com.proagende.util;

import br.com.proagende.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Conversão entre texto (dd/MM/yyyy e HH:mm) e LocalDateTime.
 * Antes essa lógica ficava nas telas Swing, que montavam a data/hora como String.
 */
public final class DataHoraUtil {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_COMPLETO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DataHoraUtil() {
    }

    public static LocalDateTime converter(String data, String hora) {
        try {
            LocalDate dia = LocalDate.parse(data == null ? "" : data.trim(), FORMATO_DATA);
            LocalTime horario = LocalTime.parse(hora == null ? "" : hora.trim(), FORMATO_HORA);
            return LocalDateTime.of(dia, horario);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Data ou hora inválida. Use dd/MM/aaaa e HH:mm.");
        }
    }

    public static String formatar(LocalDateTime dataHora) {
        return dataHora.format(FORMATO_COMPLETO);
    }
}
