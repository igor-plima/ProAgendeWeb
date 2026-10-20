package br.com.proagende.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CT-17 a CT-19: testes de integração da API REST (Etapa 9).
 * Sobem a aplicação Spring com repositórios em memória (proagende.memoria=true), sem MySQL.
 */
@SpringBootTest(properties = "proagende.memoria=true")
@AutoConfigureMockMvc
class ApiRestTest {

    @Autowired
    private MockMvc mvc;

    private int criar(String caminho, String json) throws Exception {
        MvcResult r = mvc.perform(post(caminho).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();
        String corpo = r.getResponse().getContentAsString();
        return Integer.parseInt(corpo.replaceAll("^\\{\"id\":(\\d+).*$", "$1"));
    }

    // ---------------------------------------------------------------- CT-17: clientes

    @Test
    void ct17_cadastraClienteEListaPelaApi() throws Exception {
        int id = criar("/api/clientes", "{\"nome\":\"Carlos Souza\",\"email\":\"carlos@email.com\",\"telefone\":\"11988887777\"}");
        mvc.perform(get("/api/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Souza"))
                .andExpect(jsonPath("$.telefone").value("11988887777"));
        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("carlos@email.com")));
    }

    @Test
    void ct17_emailInvalidoRetorna400ComMensagem() throws Exception {
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@\",\"telefone\":\"11999998888\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("E-mail inválido: ana@"));
    }

    @Test
    void ct17_clienteInexistenteRetorna400() throws Exception {
        mvc.perform(get("/api/clientes/9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Cliente não encontrado (id 9999)."));
    }

    // ---------------------------------------------------------------- CT-18: serviços

    @Test
    void ct18_servicoSemDescricaoEhAceitoEUsaCampoDuracao() throws Exception {
        int prof = criar("/api/profissionais", "{\"nome\":\"Ana Lima\",\"email\":\"ana@email.com\",\"telefone\":\"11999998888\"}");
        mvc.perform(post("/api/servicos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProfissional\":" + prof + ",\"nome\":\"Corte\",\"preco\":50,\"duracao\":60}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value(""))
                .andExpect(jsonPath("$.duracao").value(60));
    }

    @Test
    void ct18_naoExcluiProfissionalComServicos() throws Exception {
        int prof = criar("/api/profissionais", "{\"nome\":\"Bruno Alves\",\"email\":\"bruno@email.com\",\"telefone\":\"1133334444\"}");
        criar("/api/servicos", "{\"idProfissional\":" + prof + ",\"nome\":\"Massagem\",\"descricao\":\"Relaxante\",\"preco\":120,\"duracao\":90}");
        mvc.perform(delete("/api/profissionais/" + prof))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Não é possível excluir: o profissional possui serviços cadastrados."));
    }

    // ---------------------------------------------------------------- CT-19: agendamentos

    @Test
    void ct19_agendaComHoraLocalStatusPadraoEConflito() throws Exception {
        int prof = criar("/api/profissionais", "{\"nome\":\"Fernanda\",\"email\":\"fernanda@email.com\",\"telefone\":\"11955550000\"}");
        int serv = criar("/api/servicos", "{\"idProfissional\":" + prof + ",\"nome\":\"Escova\",\"preco\":40,\"duracao\":60}");
        int cli = criar("/api/clientes", "{\"nome\":\"Marina Costa\",\"email\":\"marina@email.com\",\"telefone\":\"11977776666\"}");

        // BUG-04/BUG-07: a hora enviada volta igual (sem conversão para UTC); status vazio vira "pendente" (BUG-03)
        MvcResult r = mvc.perform(post("/api/agendamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idServico\":" + serv + ",\"idCliente\":" + cli + ",\"dataHora\":\"2030-01-15T10:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataHora").value("2030-01-15T10:00"))
                .andExpect(jsonPath("$.status").value("pendente"))
                .andReturn();
        int id = Integer.parseInt(r.getResponse().getContentAsString().replaceAll("^\\{\"id\":(\\d+).*$", "$1"));

        // 10:30 sobrepõe o atendimento das 10:00 às 11:00 do mesmo profissional
        mvc.perform(post("/api/agendamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idServico\":" + serv + ",\"idCliente\":" + cli + ",\"dataHora\":\"2030-01-15T10:30\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("Conflito de horário")));

        mvc.perform(patch("/api/agendamentos/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"cancelado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelado"));
    }

    @Test
    void ct19_statusDesconhecidoEDataInvalidaRetornam400() throws Exception {
        mvc.perform(post("/api/agendamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idServico\":1,\"idCliente\":1,\"dataHora\":\"15/01/2030 10:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("Dados inválidos")));
        mvc.perform(patch("/api/agendamentos/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"finalizado\"}"))
                .andExpect(status().isBadRequest());
    }
}
