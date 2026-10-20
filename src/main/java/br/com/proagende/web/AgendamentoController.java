package br.com.proagende.web;

import br.com.proagende.model.Agendamento;
import br.com.proagende.model.StatusAgendamento;
import br.com.proagende.service.AgendamentoService;
import br.com.proagende.web.dto.AgendamentoDto;
import br.com.proagende.web.dto.StatusDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    /** GET /api/agendamentos, com filtros opcionais ?servico=1 e ?cliente=2. Ordenado por data/hora. */
    @GetMapping
    public List<AgendamentoDto> listar(@RequestParam(required = false) Integer servico,
                                       @RequestParam(required = false) Integer cliente) {
        List<Agendamento> lista;
        if (servico != null && cliente != null) {
            lista = service.listarPorServicoECliente(servico, cliente);
        } else if (servico != null) {
            lista = service.listarPorServico(servico);
        } else {
            lista = service.listar();
        }
        return lista.stream()
                .filter(a -> cliente == null || a.getIdCliente() == cliente)
                .sorted(Comparator.comparing(Agendamento::getDataHora))
                .map(AgendamentoDto::de)
                .toList();
    }

    @GetMapping("/{id}")
    public AgendamentoDto buscar(@PathVariable int id) {
        return AgendamentoDto.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoDto agendar(@RequestBody AgendamentoDto dto) {
        return AgendamentoDto.de(service.agendar(dto.idServico(), dto.idCliente(), dto.dataHora(),
                converterStatus(dto.status())));
    }

    /** Confirmar, cancelar ou reativar: PATCH /api/agendamentos/{id}/status com { "status": "confirmado" }. */
    @PatchMapping("/{id}/status")
    public AgendamentoDto alterarStatus(@PathVariable int id, @RequestBody StatusDto dto) {
        return AgendamentoDto.de(service.alterarStatus(id, converterStatus(dto.status())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable int id) {
        service.excluir(id);
    }

    /** Vazio ou nulo vira PENDENTE; valor desconhecido gera IllegalArgumentException (HTTP 400). */
    private static StatusAgendamento converterStatus(String status) {
        return StatusAgendamento.deValorBanco(status == null || status.isBlank() ? null : status.trim());
    }
}
