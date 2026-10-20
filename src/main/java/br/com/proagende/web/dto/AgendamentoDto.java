package br.com.proagende.web.dto;

import br.com.proagende.model.Agendamento;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * JSON de agendamento.
 * - status: texto em minúsculas ("pendente", "confirmado", "cancelado"), igual ao ENUM do MySQL e ao front-end (BUG-03).
 * - dataHora: hora local no formato ISO-8601 sem fuso, ex.: "2026-10-21T10:00" (BUG-04).
 */
public record AgendamentoDto(int id, int idServico, int idCliente,
                             @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataHora,
                             String status) {

    public static AgendamentoDto de(Agendamento a) {
        return new AgendamentoDto(a.getIdAgendamento(), a.getIdServico(), a.getIdCliente(),
                a.getDataHora(), a.getStatus().getValorBanco());
    }
}
