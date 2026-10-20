package br.com.proagende.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CT-01 a CT-03: cálculos de agenda (sem banco, sem interface). */
class CalculoAgendaTest {

    private static final LocalDateTime DEZ_HORAS = LocalDateTime.of(2030, 1, 15, 10, 0);

    @Test
    @DisplayName("Término = início + duração do serviço")
    void calculaTerminoSomandoDuracao() {
        assertEquals(LocalDateTime.of(2030, 1, 15, 11, 30), CalculoAgenda.calcularTermino(DEZ_HORAS, 90));
    }

    @Test
    @DisplayName("Término cruza a meia-noite e muda o dia")
    void terminoPodeVirarODia() {
        LocalDateTime inicio = LocalDateTime.of(2030, 1, 15, 23, 30);
        assertEquals(LocalDateTime.of(2030, 1, 16, 0, 15), CalculoAgenda.calcularTermino(inicio, 45));
    }

    @ParameterizedTest(name = "duração {0} min -> efetiva {1} min")
    @CsvSource({"60,60", "1,1", "0,1", "-30,1"})
    @DisplayName("Duração zero ou negativa vale o mínimo de 1 minuto")
    void duracaoEfetiva(int cadastrada, int esperada) {
        assertEquals(esperada, CalculoAgenda.duracaoEfetiva(cadastrada));
    }

    @Test
    @DisplayName("Serviço sem duração ainda ocupa 1 minuto no cálculo do término")
    void terminoComDuracaoZeroUsaMinimo() {
        assertEquals(DEZ_HORAS.plusMinutes(1), CalculoAgenda.calcularTermino(DEZ_HORAS, 0));
    }

    @Test
    @DisplayName("Mesmo horário de início: há sobreposição")
    void sobreposicaoNoMesmoHorario() {
        assertTrue(CalculoAgenda.haSobreposicao(DEZ_HORAS, DEZ_HORAS.plusMinutes(60),
                DEZ_HORAS, DEZ_HORAS.plusMinutes(30)));
    }

    @Test
    @DisplayName("Início dentro do atendimento anterior (10:30 dentro de 10:00-11:00): há sobreposição")
    void sobreposicaoParcial() {
        LocalDateTime inicioB = DEZ_HORAS.plusMinutes(30);
        assertTrue(CalculoAgenda.haSobreposicao(DEZ_HORAS, DEZ_HORAS.plusMinutes(60),
                inicioB, inicioB.plusMinutes(60)));
    }

    @Test
    @DisplayName("Sobreposição é simétrica (A com B = B com A)")
    void sobreposicaoEhSimetrica() {
        LocalDateTime fimA = DEZ_HORAS.plusMinutes(60);
        LocalDateTime inicioB = DEZ_HORAS.plusMinutes(30);
        LocalDateTime fimB = inicioB.plusMinutes(60);
        assertEquals(CalculoAgenda.haSobreposicao(DEZ_HORAS, fimA, inicioB, fimB),
                CalculoAgenda.haSobreposicao(inicioB, fimB, DEZ_HORAS, fimA));
    }

    @Test
    @DisplayName("Atendimento que começa exatamente quando o outro termina (11:00): sem sobreposição")
    void intervalosQueSeTocamNaoConflitam() {
        LocalDateTime fimA = DEZ_HORAS.plusMinutes(60);
        assertFalse(CalculoAgenda.haSobreposicao(DEZ_HORAS, fimA, fimA, fimA.plusMinutes(30)));
    }

    @Test
    @DisplayName("Atendimentos em horários distantes: sem sobreposição")
    void intervalosDistantesNaoConflitam() {
        LocalDateTime tarde = DEZ_HORAS.plusHours(5);
        assertFalse(CalculoAgenda.haSobreposicao(DEZ_HORAS, DEZ_HORAS.plusMinutes(60),
                tarde, tarde.plusMinutes(60)));
    }
}
