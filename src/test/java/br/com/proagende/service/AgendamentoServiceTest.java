package br.com.proagende.service;

import br.com.proagende.ProAgendeApp;
import br.com.proagende.exception.RegraNegocioException;
import br.com.proagende.model.Agendamento;
import br.com.proagende.model.Cliente;
import br.com.proagende.model.Profissional;
import br.com.proagende.model.Servico;
import br.com.proagende.model.StatusAgendamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CT-10 a CT-16: regras de agendamento, usando repositórios em memória (sem MySQL).
 * Cada teste começa com dados novos (@BeforeEach), então um não interfere no outro.
 */
class AgendamentoServiceTest {

    private static final LocalDateTime DEZ_HORAS = LocalDateTime.of(2030, 1, 15, 10, 0);

    private AgendamentoService agendamentos;
    private Servico servicoDe60Min;
    private Cliente cliente;

    @BeforeEach
    void prepararCenario() {
        ProAgendeApp app = ProAgendeApp.emMemoria();
        Profissional profissional = app.profissionais().cadastrar("Ana Lima", "ana@email.com", "11999998888");
        servicoDe60Min = app.servicos().cadastrar(profissional.getIdProfissional(), "Corte", "Corte simples", 50.0, 60);
        cliente = app.clientes().cadastrar("Carlos Souza", "carlos@email.com", "11988887777");
        agendamentos = app.agendamentos();
    }

    private Agendamento agendar(LocalDateTime dataHora, StatusAgendamento status) {
        return agendamentos.agendar(servicoDe60Min.getIdServico(), cliente.getIdCliente(), dataHora, status);
    }

    @Test
    void semStatusInformadoOAgendamentoFicaPendente() {
        assertEquals(StatusAgendamento.PENDENTE, agendar(DEZ_HORAS, null).getStatus());
    }

    @Test
    void rejeitaDoisAtendimentosNoMesmoHorario() {
        agendar(DEZ_HORAS, StatusAgendamento.CONFIRMADO);
        RegraNegocioException e = assertThrows(RegraNegocioException.class,
                () -> agendar(DEZ_HORAS, StatusAgendamento.PENDENTE));
        assertTrue(e.getMessage().startsWith("Conflito de horário"));
    }

    @Test
    void rejeitaInicioNoMeioDeOutroAtendimento() {
        agendar(DEZ_HORAS, StatusAgendamento.CONFIRMADO);
        assertThrows(RegraNegocioException.class, () -> agendar(DEZ_HORAS.plusMinutes(30), null));
    }

    @Test
    void aceitaAtendimentoQueComecaQuandoOAnteriorTermina() {
        agendar(DEZ_HORAS, StatusAgendamento.CONFIRMADO);
        Agendamento seguinte = agendar(DEZ_HORAS.plusMinutes(60), null);
        assertEquals(DEZ_HORAS.plusMinutes(60), seguinte.getDataHora());
    }

    @Test
    void horarioDeAgendamentoCanceladoVoltaAFicarLivre() {
        Agendamento primeiro = agendar(DEZ_HORAS, StatusAgendamento.CONFIRMADO);
        agendamentos.cancelar(primeiro.getIdAgendamento());
        Agendamento novo = agendar(DEZ_HORAS, null);
        assertEquals(StatusAgendamento.PENDENTE, novo.getStatus());
    }

    @Test
    void naoReativaCanceladoSeOHorarioFoiOcupado() {
        Agendamento primeiro = agendar(DEZ_HORAS, StatusAgendamento.CONFIRMADO);
        agendamentos.cancelar(primeiro.getIdAgendamento());
        agendar(DEZ_HORAS, null);
        assertThrows(RegraNegocioException.class,
                () -> agendamentos.alterarStatus(primeiro.getIdAgendamento(), StatusAgendamento.CONFIRMADO));
    }

    @Test
    void rejeitaClienteInexistente() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class,
                () -> agendamentos.agendar(servicoDe60Min.getIdServico(), 9999, DEZ_HORAS, null));
        assertEquals("Cliente não encontrado (id 9999).", e.getMessage());
    }
}
