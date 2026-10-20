package br.com.proagende.web;

import br.com.proagende.service.ProfissionalService;
import br.com.proagende.web.dto.PessoaDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @GetMapping
    public List<PessoaDto> listar() {
        return service.listar().stream().map(PessoaDto::de).toList();
    }

    @GetMapping("/{id}")
    public PessoaDto buscar(@PathVariable int id) {
        return PessoaDto.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaDto cadastrar(@RequestBody PessoaDto dto) {
        return PessoaDto.de(service.cadastrar(dto.nome(), dto.email(), dto.telefone()));
    }

    @PutMapping("/{id}")
    public PessoaDto atualizar(@PathVariable int id, @RequestBody PessoaDto dto) {
        return PessoaDto.de(service.atualizar(id, dto.nome(), dto.email(), dto.telefone()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable int id) {
        service.excluir(id);
    }
}
