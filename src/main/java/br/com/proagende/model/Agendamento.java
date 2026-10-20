package br.com.proagende.model;

import java.time.LocalDateTime;

public class Agendamento {

    private int idAgendamento;
    private int idServico;
    private int idCliente;
    private LocalDateTime dataHora;
    private StatusAgendamento status;

    public Agendamento() {
    }

    public Agendamento(int idAgendamento, int idServico, int idCliente, LocalDateTime dataHora, StatusAgendamento status) {
        this.idAgendamento = idAgendamento;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.dataHora = dataHora;
        this.status = status;
    }

    public int getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(int idAgendamento) { this.idAgendamento = idAgendamento; }

    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
}
