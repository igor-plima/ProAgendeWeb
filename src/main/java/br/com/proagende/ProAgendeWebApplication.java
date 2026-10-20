package br.com.proagende;

import br.com.proagende.service.AgendamentoService;
import br.com.proagende.service.ClienteService;
import br.com.proagende.service.ProfissionalService;
import br.com.proagende.service.ServicoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Inicia a aplicação web. As camadas da Etapa 6 (model, repository, service, util) são
 * reaproveitadas sem Swing: o ProAgendeApp monta os services e o Spring os entrega aos controllers.
 */
@SpringBootApplication
public class ProAgendeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProAgendeWebApplication.class, args);
    }

    @Bean
    public ProAgendeApp proAgendeApp(@Value("${proagende.memoria:false}") boolean memoria) {
        return memoria ? ProAgendeApp.emMemoria() : ProAgendeApp.comJdbc();
    }

    @Bean
    public ClienteService clienteService(ProAgendeApp app) { return app.clientes(); }

    @Bean
    public ProfissionalService profissionalService(ProAgendeApp app) { return app.profissionais(); }

    @Bean
    public ServicoService servicoService(ProAgendeApp app) { return app.servicos(); }

    @Bean
    public AgendamentoService agendamentoService(ProAgendeApp app) { return app.agendamentos(); }
}
