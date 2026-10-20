/* Comunicação com o back-end Spring REST (substitui o armazenamento.js / localStorage da Etapa 8 - BUG-06). */
const Api = {
  async requisitar(metodo, caminho, corpo) {
    let resposta;
    try {
      resposta = await fetch('/api/' + caminho, {
        method: metodo,
        headers: corpo === undefined ? {} : { 'Content-Type': 'application/json' },
        body: corpo === undefined ? undefined : JSON.stringify(corpo)
      });
    } catch (e) {
      throw new RegraNegocioError('Não foi possível conectar ao servidor. Verifique se a aplicação está rodando.');
    }
    if (resposta.status === 204) return null;
    const dados = await resposta.json().catch(() => null);
    if (!resposta.ok) throw new RegraNegocioError((dados && dados.mensagem) || 'Erro no servidor (HTTP ' + resposta.status + ').');
    return dados;
  },
  listar(entidade, filtros = {}) {
    const q = new URLSearchParams(Object.entries(filtros).filter(([, v]) => v !== '' && v != null)).toString();
    return this.requisitar('GET', entidade + (q ? '?' + q : ''));
  },
  buscar(entidade, id) { return this.requisitar('GET', `${entidade}/${id}`); },
  salvar(entidade, obj) {
    return obj.id ? this.requisitar('PUT', `${entidade}/${obj.id}`, obj) : this.requisitar('POST', entidade, obj);
  },
  remover(entidade, id) { return this.requisitar('DELETE', `${entidade}/${id}`); },
  alterarStatus(id, status) { return this.requisitar('PATCH', `agendamentos/${id}/status`, { status }); }
};

/* BUG-07: o back-end trabalha com hora local ("2026-10-21T10:00"). toISOString() convertia para UTC
   e deslocava o horário em 3 horas no Brasil. Estas funções mantêm a hora local. */
const DataHora = {
  paraApi(dt) {
    const p = n => String(n).padStart(2, '0');
    return `${dt.getFullYear()}-${p(dt.getMonth() + 1)}-${p(dt.getDate())}T${p(dt.getHours())}:${p(dt.getMinutes())}`;
  },
  daApi(texto) {
    const [d, h] = texto.split('T'); const [a, m, dia] = d.split('-').map(Number); const [hh, mm] = h.split(':').map(Number);
    return new Date(a, m - 1, dia, hh, mm);
  }
};
