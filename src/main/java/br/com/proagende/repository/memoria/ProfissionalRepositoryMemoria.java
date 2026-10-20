package br.com.proagende.repository.memoria;

import br.com.proagende.model.Profissional;
import br.com.proagende.repository.ProfissionalRepository;

public class ProfissionalRepositoryMemoria extends RepositorioMemoria<Profissional> implements ProfissionalRepository {

    @Override
    protected int obterId(Profissional p) { return p.getIdProfissional(); }

    @Override
    protected void definirId(Profissional p, int id) { p.setIdProfissional(id); }
}
