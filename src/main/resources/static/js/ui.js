/* Funções de interface: mensagens, erros de campo, máscaras e menu. */
const UI = {
  mensagem(tipo, texto) {
    const el = document.getElementById('mensagem');
    el.textContent = texto;
    el.className = 'mensagem ' + (tipo === 'sucesso' ? 'sucesso' : 'erro-msg');
    el.hidden = false;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    clearTimeout(this._t);
    if (tipo === 'sucesso') this._t = setTimeout(() => el.hidden = true, 4000);
  },
  limparErros() {
    document.querySelectorAll('.erro').forEach(e => e.textContent = '');
    document.querySelectorAll('.invalido,.valido').forEach(e => e.classList.remove('invalido', 'valido'));
    document.getElementById('mensagem').hidden = true;
  },
  erroCampo(id, texto) {
    const campo = document.getElementById(id);
    campo.classList.add('invalido');
    document.getElementById('erro-' + id).textContent = texto;
  },
  /** Valida um campo e mostra o erro logo abaixo dele. Retorna o valor ou undefined. */
  validar(id, funcao) {
    const campo = document.getElementById(id);
    campo.classList.remove('invalido', 'valido');
    document.getElementById('erro-' + id).textContent = '';
    try { const v = funcao(campo.value); campo.classList.add('valido'); return v; }
    catch (e) { if (e instanceof RegraNegocioError) { this.erroCampo(id, e.message); return undefined; } throw e; }
  },
  esc(t) { return String(t ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c])); },
  mascaraSomenteDigitos(id) { const e = document.getElementById(id); e.addEventListener('input', () => e.value = e.value.replace(/\D/g, '')); },
  mascaraData(id) {
    const e = document.getElementById(id);
    e.addEventListener('input', () => {
      let v = e.value.replace(/\D/g, '').slice(0, 8);
      if (v.length > 4) v = v.slice(0, 2) + '/' + v.slice(2, 4) + '/' + v.slice(4);
      else if (v.length > 2) v = v.slice(0, 2) + '/' + v.slice(2);
      e.value = v;
    });
  },
  mascaraHora(id) {
    const e = document.getElementById(id);
    e.addEventListener('input', () => {
      let v = e.value.replace(/\D/g, '').slice(0, 4);
      if (v.length > 2) v = v.slice(0, 2) + ':' + v.slice(2);
      e.value = v;
    });
  },
  preencherSelect(id, itens, rotuloVazio) {
    const s = document.getElementById(id); const atual = s.value;
    s.innerHTML = `<option value="">${rotuloVazio}</option>` + itens.map(i => `<option value="${i.valor}">${this.esc(i.texto)}</option>`).join('');
    s.value = atual;
  },
  moeda(n) { return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }); }
};
document.addEventListener('DOMContentLoaded', () => {
  const b = document.getElementById('menuBtn');
  if (b) b.addEventListener('click', () => document.getElementById('menu').classList.toggle('aberto'));
});
