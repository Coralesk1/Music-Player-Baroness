package com.otaviogustavo;

import java.util.Objects;

public class PlayList {

    private String nome;
    private String descricao;
    private String dtCriacao;

    public PlayList() { // construtor vazio por causa do Gson conseguir fazer a deserialização do json
    }

    public PlayList(String nome, String descricao, String dtCriacao){
        this.nome = nome;
        this.descricao = descricao;
        this.dtCriacao = dtCriacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(String dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         PlayList playList = (PlayList) o;
         return Objects.equals(nome, playList.nome) && Objects.equals(descricao, playList.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, descricao);
    }

}
