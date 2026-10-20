package br.com.proagende.web.dto;

/** Corpo do PATCH /api/agendamentos/{id}/status: { "status": "confirmado" }. */
public record StatusDto(String status) {
}
