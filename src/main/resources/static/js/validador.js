/* Validações no navegador - espelham as regras da classe Validador/DataHoraUtil do ProAgende (Java). */
class RegraNegocioError extends Error {}

const Validador = {
  exigirTexto(valor, campo, max) {
    const v = (valor || '').trim();
    if (!v) throw new RegraNegocioError(`O campo ${campo} é obrigatório.`);
    if (v.length > max) throw new RegraNegocioError(`O campo ${campo} deve ter no máximo ${max} caracteres.`);
    return v;
  },
  exigirEmail(valor) {
    const v = (valor || '').trim();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) throw new RegraNegocioError('E-mail inválido: informe no formato nome@dominio.com.');
    return v;
  },
  exigirTelefone(valor) {
    const v = (valor || '').trim();
    if (!/^\d{10,11}$/.test(v)) throw new RegraNegocioError('Telefone inválido: informe DDD + número, somente dígitos (10 ou 11).');
    return v;
  },
  exigirPositivo(valor, campo) {
    const n = typeof valor === 'number' ? valor : parseFloat(String(valor).replace(',', '.'));
    if (isNaN(n) || n <= 0) throw new RegraNegocioError(`O campo ${campo} deve ser maior que zero.`);
    return n;
  },
  exigirSelecao(valor, campo) {
    if (!valor) throw new RegraNegocioError(`Selecione ${campo}.`);
    return Number(valor);
  },
  /** Converte "dd/MM/aaaa" + "HH:mm" em Date; rejeita datas inexistentes (ex.: 31/02/2030). */
  converterDataHora(data, hora) {
    const msg = 'Data ou hora inválida. Use dd/MM/aaaa e HH:mm.';
    const d = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((data || '').trim());
    const h = /^([01]\d|2[0-3]):([0-5]\d)$/.exec((hora || '').trim());
    if (!d || !h) throw new RegraNegocioError(msg);
    const [dia, mes, ano] = [+d[1], +d[2], +d[3]];
    const dt = new Date(ano, mes - 1, dia, +h[1], +h[2]);
    if (dt.getFullYear() !== ano || dt.getMonth() !== mes - 1 || dt.getDate() !== dia) throw new RegraNegocioError(msg);
    return dt;
  },
  formatarDataHora(dt) {
    const p = n => String(n).padStart(2, '0');
    return `${p(dt.getDate())}/${p(dt.getMonth() + 1)}/${dt.getFullYear()} ${p(dt.getHours())}:${p(dt.getMinutes())}`;
  },
  haSobreposicao(iniA, fimA, iniB, fimB) { return iniA < fimB && iniB < fimA; }
};
