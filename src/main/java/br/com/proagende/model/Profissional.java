package br.com.proagende.model;

public class Profissional {

    private int idProfissional;
    private String nome;
    private String email;
    private String telefone;

    public Profissional() {
    }

    public Profissional(int idProfissional, String nome, String email, String telefone) {
        this.idProfissional = idProfissional;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return nome;
    }
}
