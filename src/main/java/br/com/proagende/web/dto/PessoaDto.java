package br.com.proagende.web.dto;

import br.com.proagende.model.Cliente;
import br.com.proagende.model.Profissional;

/** JSON de cliente e profissional: { "id", "nome", "email", "telefone" } (mesmos nomes usados pelo front-end). */
public record PessoaDto(int id, String nome, String email, String telefone) {

    public static PessoaDto de(Cliente c) {
        return new PessoaDto(c.getIdCliente(), c.getNome(), c.getEmail(), c.getTelefone());
    }

    public static PessoaDto de(Profissional p) {
        return new PessoaDto(p.getIdProfissional(), p.getNome(), p.getEmail(), p.getTelefone());
    }
}
