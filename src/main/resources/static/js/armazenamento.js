/* Simula o "banco" usando localStorage (apenas para a etapa de front-end; sem back-end). */
const Armazenamento = {
  listar(entidade) { return JSON.parse(localStorage.getItem('proagende_' + entidade) || '[]'); },
  gravar(entidade, lista) { localStorage.setItem('proagende_' + entidade, JSON.stringify(lista)); },
  proximoId(entidade) { return this.listar(entidade).reduce((m, x) => Math.max(m, x.id), 0) + 1; },
  buscar(entidade, id) { return this.listar(entidade).find(x => x.id === Number(id)); },
  salvar(entidade, obj) {
    const lista = this.listar(entidade);
    if (obj.id) { const i = lista.findIndex(x => x.id === obj.id); lista[i] = obj; }
    else { obj.id = this.proximoId(entidade); lista.push(obj); }
    this.gravar(entidade, lista);
    return obj;
  },
  remover(entidade, id) { this.gravar(entidade, this.listar(entidade).filter(x => x.id !== Number(id))); },
  limparTudo() { ['clientes', 'profissionais', 'servicos', 'agendamentos'].forEach(e => localStorage.removeItem('proagende_' + e)); }
};
