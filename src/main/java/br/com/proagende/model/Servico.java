package br.com.proagende.model;

public class Servico {

    private int idServico;
    private int idProfissional;
    private String nome;
    private String descricao;
    private double preco;
    private int duracaoMinutos;

    public Servico() {
    }

    public Servico(int idServico, int idProfissional, String nome, String descricao, double preco, int duracaoMinutos) {
        this.idServico = idServico;
        this.idProfissional = idProfissional;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
    }

    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    @Override
    public String toString() {
        return nome;
    }
}
