package br.com.proagende.util;

import java.time.LocalDateTime;

/** Cálculos puros de agenda (sem banco), usados pelo AgendamentoService e testados em CalculoAgendaTest. */
public final class CalculoAgenda {

    private CalculoAgenda() {
    }

    /** Serviço sem duração cadastrada ocupa ao menos 1 minuto, para que dois no mesmo instante conflitem. */
    public static int duracaoEfetiva(int minutos) {
        return minutos > 0 ? minutos : 1;
    }

    public static LocalDateTime calcularTermino(LocalDateTime inicio, int minutos) {
        return inicio.plusMinutes(duracaoEfetiva(minutos));
    }

    /** Intervalos [iniA, fimA) e [iniB, fimB) se sobrepõem? Encostar (fim de um = início do outro) não conflita. */
    public static boolean haSobreposicao(LocalDateTime iniA, LocalDateTime fimA,
                                         LocalDateTime iniB, LocalDateTime fimB) {
        return iniA.isBefore(fimB) && iniB.isBefore(fimA);
    }
}
