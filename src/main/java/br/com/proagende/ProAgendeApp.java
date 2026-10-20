package br.com.proagende;

import br.com.proagende.config.ConexaoBanco;
import br.com.proagende.config.FabricaConexao;
import br.com.proagende.repository.AgendamentoRepository;
import br.com.proagende.repository.ClienteRepository;
import br.com.proagende.repository.ProfissionalRepository;
import br.com.proagende.repository.ServicoRepository;
import br.com.proagende.repository.jdbc.AgendamentoRepositoryJdbc;
import br.com.proagende.repository.jdbc.ClienteRepositoryJdbc;
import br.com.proagende.repository.jdbc.ProfissionalRepositoryJdbc;
import br.com.proagende.repository.jdbc.ServicoRepositoryJdbc;
import br.com.proagende.repository.memoria.AgendamentoRepositoryMemoria;
import br.com.proagende.repository.memoria.ClienteRepositoryMemoria;
import br.com.proagende.repository.memoria.ProfissionalRepositoryMemoria;
import br.com.proagende.repository.memoria.ServicoRepositoryMemoria;
import br.com.proagende.service.AgendamentoService;
import br.com.proagende.service.ClienteService;
import br.com.proagende.service.ProfissionalService;
import br.com.proagende.service.ServicoService;

/**
 * Ponto único de montagem das dependências (injeção manual por construtor).
 * A futura aplicação web pode chamar {@link #comJdbc()} e usar os services diretamente.
 */
public class ProAgendeApp {

    private final ProfissionalService profissionalService;
    private final ClienteService clienteService;
    private final ServicoService servicoService;
    private final AgendamentoService agendamentoService;

    private ProAgendeApp(ProfissionalRepository profissionais, ClienteRepository clientes,
                         ServicoRepository servicos, AgendamentoRepository agendamentos) {
        this.profissionalService = new ProfissionalService(profissionais, servicos);
        this.clienteService = new ClienteService(clientes, agendamentos);
        this.servicoService = new ServicoService(servicos, profissionais, agendamentos);
        this.agendamentoService = new AgendamentoService(agendamentos, servicos, clientes);
    }

    /** Usa MySQL com a configuração de banco.properties / variáveis de ambiente. */
    public static ProAgendeApp comJdbc() {
        return comJdbc(ConexaoBanco.aPartirDoAmbiente());
    }

    public static ProAgendeApp comJdbc(FabricaConexao fabrica) {
        return new ProAgendeApp(
                new ProfissionalRepositoryJdbc(fabrica),
                new ClienteRepositoryJdbc(fabrica),
                new ServicoRepositoryJdbc(fabrica),
                new AgendamentoRepositoryJdbc(fabrica));
    }

    /** Usa repositórios em memória: ideal para testar as regras sem banco de dados. */
    public static ProAgendeApp emMemoria() {
        ServicoRepositoryMemoria servicos = new ServicoRepositoryMemoria();
        return new ProAgendeApp(
                new ProfissionalRepositoryMemoria(),
                new ClienteRepositoryMemoria(),
                servicos,
                new AgendamentoRepositoryMemoria(servicos));
    }

    public ProfissionalService profissionais() { return profissionalService; }
    public ClienteService clientes() { return clienteService; }
    public ServicoService servicos() { return servicoService; }
    public AgendamentoService agendamentos() { return agendamentoService; }
}
