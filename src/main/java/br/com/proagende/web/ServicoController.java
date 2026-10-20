package br.com.proagende.web;

import br.com.proagende.service.ServicoService;
import br.com.proagende.web.dto.ServicoDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    /** GET /api/servicos ou /api/servicos?profissional=1 */
    @GetMapping
    public List<ServicoDto> listar(@RequestParam(required = false) Integer profissional) {
        var lista = profissional == null ? service.listar() : service.listarPorProfissional(profissional);
        return lista.stream().map(ServicoDto::de).toList();
    }

    @GetMapping("/{id}")
    public ServicoDto buscar(@PathVariable int id) {
        return ServicoDto.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoDto cadastrar(@RequestBody ServicoDto dto) {
        return ServicoDto.de(service.cadastrar(dto.idProfissional(), dto.nome(), dto.descricao(),
                dto.preco(), dto.duracao()));
    }

    @PutMapping("/{id}")
    public ServicoDto atualizar(@PathVariable int id, @RequestBody ServicoDto dto) {
        return ServicoDto.de(service.atualizar(id, dto.idProfissional(), dto.nome(), dto.descricao(),
                dto.preco(), dto.duracao()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable int id) {
        service.excluir(id);
    }
}
