package br.com.proagende.web.dto;

import br.com.proagende.model.Servico;

/**
 * JSON de serviço. O front-end da Etapa 8 usa o campo "duracao";
 * no model Java ele se chama duracaoMinutos (BUG-03).
 */
public record ServicoDto(int id, int idProfissional, String nome, String descricao, double preco, int duracao) {

    public static ServicoDto de(Servico s) {
        return new ServicoDto(s.getIdServico(), s.getIdProfissional(), s.getNome(),
                s.getDescricao() == null ? "" : s.getDescricao(), s.getPreco(), s.getDuracaoMinutos());
    }
}
