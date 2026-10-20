package br.com.proagende.repository;

import br.com.proagende.model.Agendamento;

import java.util.List;

public interface AgendamentoRepository extends Repository<Agendamento> {

    List<Agendamento> listarPorServico(int idServico);

    List<Agendamento> listarPorCliente(int idCliente);

    List<Agendamento> listarPorServicoECliente(int idServico, int idCliente);

    /** Todos os agendamentos dos serviços oferecidos por um profissional (base do controle de conflito). */
    List<Agendamento> listarPorProfissional(int idProfissional);
}
