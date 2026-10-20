/* Lógica de cada página, agora consumindo a API REST. A página é identificada por <body data-pagina="...">. */
document.addEventListener('DOMContentLoaded', () => {
  const pagina = document.body.dataset.pagina;
  ({ inicio: paginaInicio, clientes: () => cadastroPessoa('clientes', 'cliente'),
     profissionais: () => cadastroPessoa('profissionais', 'profissional'),
     servicos: paginaServicos, agendamentos: paginaAgendamentos })[pagina]();
});

const el = id => document.getElementById(id);
const rotulosStatus = { pendente: 'Pendente', confirmado: 'Confirmado', cancelado: 'Cancelado' };
const porId = lista => Object.fromEntries(lista.map(x => [x.id, x]));
const nome = (mapa, id) => (mapa[id] || { nome: '(removido)' }).nome;

/** Executa uma ação assíncrona e mostra qualquer erro (validação do servidor, conflito, rede) na tela. */
async function executar(acao) {
  try { return await acao(); } catch (e) { UI.mensagem('erro', e.message); return undefined; }
}

/* ---------- Clientes e Profissionais (mesma estrutura) ---------- */
function cadastroPessoa(entidade, rotulo) {
  UI.mascaraSomenteDigitos('telefone');
  const form = el('formulario');
  let cache = {};
  const novoTitulo = () => { el('id').value = ''; el('tituloForm').textContent = 'Novo ' + rotulo; };
  const desenhar = () => executar(async () => {
    const lista = await Api.listar(entidade);
    cache = porId(lista);
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(p => `<tr><td>${p.id}</td><td>${UI.esc(p.nome)}</td><td>${UI.esc(p.email)}</td><td>${UI.esc(p.telefone)}</td>
      <td><button class="btn pequeno" data-acao="editar" data-id="${p.id}">Editar</button>
      <button class="btn pequeno perigo" data-acao="excluir" data-id="${p.id}">Excluir</button></td></tr>`).join('');
  });
  form.addEventListener('submit', async ev => {
    ev.preventDefault();
    const nomeV = UI.validar('nome', v => Validador.exigirTexto(v, 'Nome', 100));
    const email = UI.validar('email', Validador.exigirEmail);
    const telefone = UI.validar('telefone', Validador.exigirTelefone);
    if (nomeV === undefined || email === undefined || telefone === undefined) return UI.mensagem('erro', 'Corrija os campos destacados.');
    const ok = await executar(() => Api.salvar(entidade, { id: Number(el('id').value) || 0, nome: nomeV, email, telefone }));
    if (!ok) return;
    form.reset(); novoTitulo(); UI.limparErros();
    UI.mensagem('sucesso', `${rotulo[0].toUpperCase() + rotulo.slice(1)} salvo com sucesso.`);
    desenhar();
  });
  el('btnLimpar').addEventListener('click', () => { novoTitulo(); UI.limparErros(); });
  el('corpoTabela').addEventListener('click', async ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const id = Number(b.dataset.id);
    if (b.dataset.acao === 'editar') {
      const p = cache[id]; UI.limparErros();
      el('id').value = p.id; el('nome').value = p.nome; el('email').value = p.email; el('telefone').value = p.telefone;
      el('tituloForm').textContent = 'Editar ' + rotulo; window.scrollTo({ top: 0, behavior: 'smooth' });
    } else if (confirm('Excluir este ' + rotulo + '?')) {
      // A regra "possui agendamentos/serviços" agora é verificada no servidor (ClienteService/ProfissionalService).
      const ok = await executar(async () => { await Api.remover(entidade, id); return true; });
      if (ok) { UI.mensagem('sucesso', 'Registro excluído.'); desenhar(); }
    }
  });
  desenhar();
}

/* ---------- Serviços ---------- */
function paginaServicos() {
  const form = el('formulario');
  let cache = {};
  const novoTitulo = () => { el('id').value = ''; el('tituloForm').textContent = 'Novo serviço'; };
  const desenhar = () => executar(async () => {
    const [profissionais, lista] = await Promise.all([Api.listar('profissionais'), Api.listar('servicos')]);
    const profs = porId(profissionais);
    cache = porId(lista);
    UI.preencherSelect('idProfissional', profissionais.map(p => ({ valor: p.id, texto: p.nome })), 'Selecione...');
    if (profissionais.length === 0) UI.mensagem('erro', 'Cadastre ao menos um profissional antes de criar serviços.');
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(s => `<tr><td>${s.id}</td><td>${UI.esc(s.nome)}<br><small>${UI.esc(s.descricao)}</small></td>
      <td>${UI.esc(nome(profs, s.idProfissional))}</td><td>${UI.moeda(s.preco)}</td><td>${s.duracao} min</td>
      <td><button class="btn pequeno" data-acao="editar" data-id="${s.id}">Editar</button>
      <button class="btn pequeno perigo" data-acao="excluir" data-id="${s.id}">Excluir</button></td></tr>`).join('');
  });
  form.addEventListener('submit', async ev => {
    ev.preventDefault();
    const idProfissional = UI.validar('idProfissional', v => Validador.exigirSelecao(v, 'o profissional'));
    const nomeV = UI.validar('nome', v => Validador.exigirTexto(v, 'Nome do serviço', 100));
    const descricao = (el('descricao').value || '').trim();
    const preco = UI.validar('preco', v => Validador.exigirPositivo(v, 'Preço'));
    const duracao = UI.validar('duracao', v => Math.round(Validador.exigirPositivo(v, 'Duração')));
    if ([idProfissional, nomeV, preco, duracao].includes(undefined)) return UI.mensagem('erro', 'Corrija os campos destacados.');
    const ok = await executar(() => Api.salvar('servicos', { id: Number(el('id').value) || 0, idProfissional, nome: nomeV, descricao, preco, duracao }));
    if (!ok) return;
    form.reset(); novoTitulo(); UI.limparErros();
    UI.mensagem('sucesso', 'Serviço salvo com sucesso.');
    desenhar();
  });
  el('btnLimpar').addEventListener('click', () => { novoTitulo(); UI.limparErros(); });
  el('corpoTabela').addEventListener('click', async ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const id = Number(b.dataset.id);
    if (b.dataset.acao === 'editar') {
      const s = cache[id]; UI.limparErros();
      el('id').value = s.id; el('idProfissional').value = s.idProfissional; el('nome').value = s.nome;
      el('descricao').value = s.descricao; el('preco').value = String(s.preco).replace('.', ','); el('duracao').value = s.duracao;
      el('tituloForm').textContent = 'Editar serviço'; window.scrollTo({ top: 0, behavior: 'smooth' });
    } else if (confirm('Excluir este serviço?')) {
      const ok = await executar(async () => { await Api.remover('servicos', id); return true; });
      if (ok) { UI.mensagem('sucesso', 'Serviço excluído.'); desenhar(); }
    }
  });
  desenhar();
}

/* ---------- Agendamentos ---------- */
function paginaAgendamentos() {
  UI.mascaraData('data'); UI.mascaraHora('hora');
  const form = el('formulario');
  UI.preencherSelect('status', Object.entries(rotulosStatus).map(([v, t]) => ({ valor: v, texto: t })), 'Pendente (padrão)');
  const desenhar = () => executar(async () => {
    const [servicos, clientes, profissionais, todos] = await Promise.all(
      [Api.listar('servicos'), Api.listar('clientes'), Api.listar('profissionais'), Api.listar('agendamentos')]);
    const svs = porId(servicos), cls = porId(clientes), profs = porId(profissionais);
    UI.preencherSelect('idServico', servicos.map(s => ({ valor: s.id, texto: `${s.nome} (${s.duracao} min) - ${nome(profs, s.idProfissional)}` })), 'Selecione...');
    UI.preencherSelect('idCliente', clientes.map(c => ({ valor: c.id, texto: c.nome })), 'Selecione...');
    if (!servicos.length || !clientes.length) UI.mensagem('erro', 'Cadastre ao menos um cliente e um serviço para agendar.');
    // filtros sem repetição: só serviços/clientes que têm agendamento
    const unicos = (campo, mapa) => [...new Set(todos.map(a => a[campo]))].map(id => ({ valor: id, texto: nome(mapa, id) }));
    UI.preencherSelect('filtroServico', unicos('idServico', svs), 'Todos os serviços');
    UI.preencherSelect('filtroCliente', unicos('idCliente', cls), 'Todos os clientes');
    // a filtragem e a ordenação por data/hora são feitas pelo servidor (GET /api/agendamentos?servico=&cliente=)
    const lista = await Api.listar('agendamentos', { servico: el('filtroServico').value, cliente: el('filtroCliente').value });
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(a => {
      const sv = svs[a.idServico] || {};
      const acoes = a.status === 'cancelado'
        ? `<button class="btn pequeno" data-acao="pendente" data-id="${a.id}">Reativar</button>`
        : `<button class="btn pequeno" data-acao="confirmado" data-id="${a.id}">Confirmar</button>
           <button class="btn pequeno perigo" data-acao="cancelado" data-id="${a.id}">Cancelar</button>`;
      return `<tr><td>${a.id}</td><td>${Validador.formatarDataHora(DataHora.daApi(a.dataHora))}</td><td>${UI.esc(nome(cls, a.idCliente))}</td>
        <td>${UI.esc(sv.nome)}</td><td>${UI.esc(nome(profs, sv.idProfissional))}</td>
        <td><span class="selo ${a.status}">${rotulosStatus[a.status]}</span></td><td>${acoes}</td></tr>`;
    }).join('');
  });
  form.addEventListener('submit', async ev => {
    ev.preventDefault();
    const idServico = UI.validar('idServico', v => Validador.exigirSelecao(v, 'o serviço'));
    const idCliente = UI.validar('idCliente', v => Validador.exigirSelecao(v, 'o cliente'));
    let dataHora;
    try { dataHora = Validador.converterDataHora(el('data').value, el('hora').value); el('data').classList.add('valido'); el('hora').classList.add('valido'); }
    catch (e) { UI.erroCampo('data', e.message); UI.erroCampo('hora', ''); }
    if (idServico === undefined || idCliente === undefined || !dataHora) return UI.mensagem('erro', 'Corrija os campos destacados.');
    // o conflito de horário é verificado pelo AgendamentoService; a mensagem do servidor é exibida em caso de erro
    const ok = await executar(() => Api.salvar('agendamentos',
      { id: 0, idServico, idCliente, dataHora: DataHora.paraApi(dataHora), status: el('status').value || 'pendente' }));
    if (!ok) return;
    form.reset(); UI.limparErros();
    UI.mensagem('sucesso', 'Agendamento registrado com sucesso.');
    desenhar();
  });
  el('btnLimpar').addEventListener('click', () => UI.limparErros());
  el('filtroServico').addEventListener('change', desenhar);
  el('filtroCliente').addEventListener('change', desenhar);
  el('corpoTabela').addEventListener('click', async ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const novo = b.dataset.acao;
    const ok = await executar(() => Api.alterarStatus(b.dataset.id, novo));
    if (ok) { UI.mensagem('sucesso', 'Status alterado para ' + rotulosStatus[novo].toLowerCase() + '.'); desenhar(); }
  });
  desenhar();
}

/* ---------- Painel ---------- */
function paginaInicio() {
  executar(async () => {
    const [clientes, profissionais, servicos, agendamentos] = await Promise.all(
      [Api.listar('clientes'), Api.listar('profissionais'), Api.listar('servicos'), Api.listar('agendamentos')]);
    const svs = porId(servicos), cls = porId(clientes), profs = porId(profissionais);
    const ativos = agendamentos.filter(a => a.status !== 'cancelado');
    el('qtdClientes').textContent = clientes.length;
    el('qtdProfissionais').textContent = profissionais.length;
    el('qtdServicos').textContent = servicos.length;
    el('qtdAgendamentos').textContent = ativos.length;
    const agora = new Date();
    const prox = ativos.filter(a => DataHora.daApi(a.dataHora) >= agora).slice(0, 8); // já vem ordenado pelo servidor
    el('vazio').hidden = prox.length > 0;
    el('corpoTabela').innerHTML = prox.map(a => {
      const sv = svs[a.idServico] || {};
      return `<tr><td>${Validador.formatarDataHora(DataHora.daApi(a.dataHora))}</td><td>${UI.esc(nome(cls, a.idCliente))}</td><td>${UI.esc(sv.nome)}</td>
        <td>${UI.esc(nome(profs, sv.idProfissional))}</td><td><span class="selo ${a.status}">${rotulosStatus[a.status]}</span></td></tr>`;
    }).join('');
  });
}
