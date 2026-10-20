/* Lógica de cada página. A página é identificada por <body data-pagina="...">. */
document.addEventListener('DOMContentLoaded', () => {
  const pagina = document.body.dataset.pagina;
  ({ inicio: paginaInicio, clientes: () => cadastroPessoa('clientes', 'cliente'),
     profissionais: () => cadastroPessoa('profissionais', 'profissional'),
     servicos: paginaServicos, agendamentos: paginaAgendamentos })[pagina]();
});

const A = Armazenamento;
const el = id => document.getElementById(id);
const nomeDe = (ent, id) => (A.buscar(ent, id) || { nome: '(removido)' }).nome;

function duracaoEfetiva(min) { return min > 0 ? min : 1; }
function termino(inicio, min) { return new Date(inicio.getTime() + duracaoEfetiva(min) * 60000); }

/* ---------- Clientes e Profissionais (mesma estrutura) ---------- */
function cadastroPessoa(entidade, rotulo) {
  UI.mascaraSomenteDigitos('telefone');
  const form = el('formulario');
  const desenhar = () => {
    const lista = A.listar(entidade);
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(p => `<tr><td>${p.id}</td><td>${UI.esc(p.nome)}</td><td>${UI.esc(p.email)}</td><td>${UI.esc(p.telefone)}</td>
      <td><button class="btn pequeno" data-acao="editar" data-id="${p.id}">Editar</button>
      <button class="btn pequeno perigo" data-acao="excluir" data-id="${p.id}">Excluir</button></td></tr>`).join('');
  };
  form.addEventListener('submit', ev => {
    ev.preventDefault();
    const nome = UI.validar('nome', v => Validador.exigirTexto(v, 'Nome', 100));
    const email = UI.validar('email', Validador.exigirEmail);
    const telefone = UI.validar('telefone', Validador.exigirTelefone);
    if (nome === undefined || email === undefined || telefone === undefined) return UI.mensagem('erro', 'Corrija os campos destacados.');
    A.salvar(entidade, { id: Number(el('id').value) || 0, nome, email, telefone });
    UI.mensagem('sucesso', `${rotulo[0].toUpperCase() + rotulo.slice(1)} salvo com sucesso.`);
    form.reset(); el('id').value = ''; el('tituloForm').textContent = 'Novo ' + rotulo; UI.limparErros(); desenhar();
  });
  el('btnLimpar').addEventListener('click', () => { el('id').value = ''; el('tituloForm').textContent = 'Novo ' + rotulo; UI.limparErros(); });
  el('corpoTabela').addEventListener('click', ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const id = Number(b.dataset.id);
    if (b.dataset.acao === 'editar') {
      const p = A.buscar(entidade, id); UI.limparErros();
      el('id').value = p.id; el('nome').value = p.nome; el('email').value = p.email; el('telefone').value = p.telefone;
      el('tituloForm').textContent = 'Editar ' + rotulo; window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      const temDependentes = entidade === 'clientes'
        ? A.listar('agendamentos').some(a => a.idCliente === id)
        : A.listar('servicos').some(s => s.idProfissional === id);
      if (temDependentes) return UI.mensagem('erro', entidade === 'clientes'
        ? 'Não é possível excluir: o cliente possui agendamentos.' : 'Não é possível excluir: o profissional possui serviços.');
      if (confirm('Excluir este ' + rotulo + '?')) { A.remover(entidade, id); UI.mensagem('sucesso', 'Registro excluído.'); desenhar(); }
    }
  });
  desenhar();
}

/* ---------- Serviços ---------- */
function paginaServicos() {
  const form = el('formulario');
  const carregarProfissionais = () => UI.preencherSelect('idProfissional',
    A.listar('profissionais').map(p => ({ valor: p.id, texto: p.nome })), 'Selecione...');
  const desenhar = () => {
    carregarProfissionais();
    const lista = A.listar('servicos');
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(s => `<tr><td>${s.id}</td><td>${UI.esc(s.nome)}<br><small>${UI.esc(s.descricao)}</small></td>
      <td>${UI.esc(nomeDe('profissionais', s.idProfissional))}</td><td>${UI.moeda(s.preco)}</td><td>${s.duracao} min</td>
      <td><button class="btn pequeno" data-acao="editar" data-id="${s.id}">Editar</button>
      <button class="btn pequeno perigo" data-acao="excluir" data-id="${s.id}">Excluir</button></td></tr>`).join('');
  };
  if (A.listar('profissionais').length === 0) UI.mensagem('erro', 'Cadastre ao menos um profissional antes de criar serviços.');
  form.addEventListener('submit', ev => {
    ev.preventDefault();
    const idProfissional = UI.validar('idProfissional', v => Validador.exigirSelecao(v, 'o profissional'));
    const nome = UI.validar('nome', v => Validador.exigirTexto(v, 'Nome do serviço', 100));
    const descricao = (el('descricao').value || '').trim();
    const preco = UI.validar('preco', v => Validador.exigirPositivo(v, 'Preço'));
    const duracao = UI.validar('duracao', v => Math.round(Validador.exigirPositivo(v, 'Duração')));
    if ([idProfissional, nome, preco, duracao].includes(undefined)) return UI.mensagem('erro', 'Corrija os campos destacados.');
    A.salvar('servicos', { id: Number(el('id').value) || 0, idProfissional, nome, descricao, preco, duracao });
    UI.mensagem('sucesso', 'Serviço salvo com sucesso.');
    form.reset(); el('id').value = ''; el('tituloForm').textContent = 'Novo serviço'; UI.limparErros(); desenhar();
  });
  el('btnLimpar').addEventListener('click', () => { el('id').value = ''; el('tituloForm').textContent = 'Novo serviço'; UI.limparErros(); });
  el('corpoTabela').addEventListener('click', ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const id = Number(b.dataset.id);
    if (b.dataset.acao === 'editar') {
      const s = A.buscar('servicos', id); UI.limparErros();
      el('id').value = s.id; el('idProfissional').value = s.idProfissional; el('nome').value = s.nome;
      el('descricao').value = s.descricao; el('preco').value = String(s.preco).replace('.', ','); el('duracao').value = s.duracao;
      el('tituloForm').textContent = 'Editar serviço'; window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      if (A.listar('agendamentos').some(a => a.idServico === id)) return UI.mensagem('erro', 'Não é possível excluir: o serviço possui agendamentos.');
      if (confirm('Excluir este serviço?')) { A.remover('servicos', id); UI.mensagem('sucesso', 'Serviço excluído.'); desenhar(); }
    }
  });
  desenhar();
}

/* ---------- Agendamentos ---------- */
function procurarConflito(idServico, inicio, ignorarId) {
  const servico = A.buscar('servicos', idServico);
  const fim = termino(inicio, servico.duracao);
  return A.listar('agendamentos').find(a => {
    if (a.id === ignorarId || a.status === 'cancelado') return false;
    const sv = A.buscar('servicos', a.idServico);
    if (!sv || sv.idProfissional !== servico.idProfissional) return false;
    const ini = new Date(a.dataHora);
    return Validador.haSobreposicao(inicio, fim, ini, termino(ini, sv.duracao));
  });
}
function lancarConflito(c) {
  throw new RegraNegocioError(`Conflito de horário: o profissional já possui o agendamento #${c.id} em ${Validador.formatarDataHora(new Date(c.dataHora))}.`);
}

function paginaAgendamentos() {
  UI.mascaraData('data'); UI.mascaraHora('hora');
  const form = el('formulario');
  const rotulos = { pendente: 'Pendente', confirmado: 'Confirmado', cancelado: 'Cancelado' };
  UI.preencherSelect('status', Object.entries(rotulos).map(([v, t]) => ({ valor: v, texto: t })), 'Pendente (padrão)');
  const desenhar = () => {
    UI.preencherSelect('idServico', A.listar('servicos').map(s => ({ valor: s.id, texto: `${s.nome} (${s.duracao} min) - ${nomeDe('profissionais', s.idProfissional)}` })), 'Selecione...');
    UI.preencherSelect('idCliente', A.listar('clientes').map(c => ({ valor: c.id, texto: c.nome })), 'Selecione...');
    const ags = A.listar('agendamentos');
    // filtros sem repetição: só serviços/clientes que têm agendamento
    const unicos = (campo, ent) => [...new Set(ags.map(a => a[campo]))].map(id => ({ valor: id, texto: nomeDe(ent, id) }));
    UI.preencherSelect('filtroServico', unicos('idServico', 'servicos'), 'Todos os serviços');
    UI.preencherSelect('filtroCliente', unicos('idCliente', 'clientes'), 'Todos os clientes');
    const fs = el('filtroServico').value, fc = el('filtroCliente').value;
    const lista = ags.filter(a => (!fs || a.idServico === +fs) && (!fc || a.idCliente === +fc))
      .sort((x, y) => new Date(x.dataHora) - new Date(y.dataHora));
    el('vazio').hidden = lista.length > 0;
    el('corpoTabela').innerHTML = lista.map(a => {
      const sv = A.buscar('servicos', a.idServico) || {};
      const acoes = a.status === 'cancelado'
        ? `<button class="btn pequeno" data-acao="pendente" data-id="${a.id}">Reativar</button>`
        : `<button class="btn pequeno" data-acao="confirmado" data-id="${a.id}">Confirmar</button>
           <button class="btn pequeno perigo" data-acao="cancelado" data-id="${a.id}">Cancelar</button>`;
      return `<tr><td>${a.id}</td><td>${Validador.formatarDataHora(new Date(a.dataHora))}</td><td>${UI.esc(nomeDe('clientes', a.idCliente))}</td>
        <td>${UI.esc(sv.nome)}</td><td>${UI.esc(nomeDe('profissionais', sv.idProfissional))}</td>
        <td><span class="selo ${a.status}">${rotulos[a.status]}</span></td><td>${acoes}</td></tr>`;
    }).join('');
  };
  if (!A.listar('servicos').length || !A.listar('clientes').length)
    UI.mensagem('erro', 'Cadastre ao menos um cliente e um serviço para agendar (ou use os dados de exemplo no Painel).');
  form.addEventListener('submit', ev => {
    ev.preventDefault();
    const idServico = UI.validar('idServico', v => Validador.exigirSelecao(v, 'o serviço'));
    const idCliente = UI.validar('idCliente', v => Validador.exigirSelecao(v, 'o cliente'));
    let dataHora;
    try { dataHora = Validador.converterDataHora(el('data').value, el('hora').value); el('data').classList.add('valido'); el('hora').classList.add('valido'); }
    catch (e) { UI.erroCampo('data', e.message); UI.erroCampo('hora', ''); }
    if (idServico === undefined || idCliente === undefined || !dataHora) return UI.mensagem('erro', 'Corrija os campos destacados.');
    try {
      const c = procurarConflito(idServico, dataHora, 0);
      if (c) lancarConflito(c);
    } catch (e) { return UI.mensagem('erro', e.message); }
    A.salvar('agendamentos', { id: 0, idServico, idCliente, dataHora: dataHora.toISOString(), status: el('status').value || 'pendente' });
    UI.mensagem('sucesso', 'Agendamento registrado com sucesso.');
    form.reset(); UI.limparErros(); desenhar();
  });
  el('btnLimpar').addEventListener('click', () => UI.limparErros());
  el('filtroServico').addEventListener('change', desenhar);
  el('filtroCliente').addEventListener('change', desenhar);
  el('corpoTabela').addEventListener('click', ev => {
    const b = ev.target.closest('button'); if (!b) return;
    const ag = A.buscar('agendamentos', b.dataset.id); const novo = b.dataset.acao;
    if (novo !== 'cancelado') { // confirmar/reativar exige horário livre
      const c = procurarConflito(ag.idServico, new Date(ag.dataHora), ag.id);
      if (c) { try { lancarConflito(c); } catch (e) { return UI.mensagem('erro', e.message); } }
    }
    ag.status = novo; A.salvar('agendamentos', ag);
    UI.mensagem('sucesso', 'Status alterado para ' + rotulos[novo].toLowerCase() + '.'); desenhar();
  });
  desenhar();
}

/* ---------- Painel ---------- */
function paginaInicio() {
  const desenhar = () => {
    const ags = A.listar('agendamentos').filter(a => a.status !== 'cancelado');
    el('qtdClientes').textContent = A.listar('clientes').length;
    el('qtdProfissionais').textContent = A.listar('profissionais').length;
    el('qtdServicos').textContent = A.listar('servicos').length;
    el('qtdAgendamentos').textContent = ags.length;
    const prox = ags.filter(a => new Date(a.dataHora) >= new Date()).sort((x, y) => new Date(x.dataHora) - new Date(y.dataHora)).slice(0, 8);
    el('vazio').hidden = prox.length > 0;
    el('corpoTabela').innerHTML = prox.map(a => {
      const sv = A.buscar('servicos', a.idServico) || {};
      return `<tr><td>${Validador.formatarDataHora(new Date(a.dataHora))}</td><td>${UI.esc(nomeDe('clientes', a.idCliente))}</td><td>${UI.esc(sv.nome)}</td>
        <td>${UI.esc(nomeDe('profissionais', sv.idProfissional))}</td><td><span class="selo ${a.status}">${a.status}</span></td></tr>`;
    }).join('');
  };
  el('btnExemplo').addEventListener('click', () => {
    A.limparTudo();
    A.gravar('profissionais', [{ id: 1, nome: 'Ana Lima', email: 'ana@email.com', telefone: '11999998888' }, { id: 2, nome: 'Bruno Alves', email: 'bruno@email.com', telefone: '1133334444' }]);
    A.gravar('clientes', [{ id: 1, nome: 'Carlos Souza', email: 'carlos@email.com', telefone: '11988887777' }, { id: 2, nome: 'Marina Costa', email: 'marina@email.com', telefone: '11977776666' }]);
    A.gravar('servicos', [{ id: 1, idProfissional: 1, nome: 'Corte', descricao: 'Corte simples', preco: 50, duracao: 60 }, { id: 2, idProfissional: 2, nome: 'Massagem', descricao: 'Relaxante', preco: 120, duracao: 90 }]);
    const amanha = new Date(); amanha.setDate(amanha.getDate() + 1); amanha.setHours(10, 0, 0, 0);
    A.gravar('agendamentos', [{ id: 1, idServico: 1, idCliente: 1, dataHora: amanha.toISOString(), status: 'confirmado' }]);
    UI.mensagem('sucesso', 'Dados de exemplo carregados.'); desenhar();
  });
  el('btnApagar').addEventListener('click', () => { if (confirm('Apagar todos os dados?')) { A.limparTudo(); UI.mensagem('sucesso', 'Dados apagados.'); desenhar(); } });
  desenhar();
}
