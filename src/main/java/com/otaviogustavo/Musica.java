package com.otaviogustavo;

public class Musica {

    private String titulo;
    private String artista;
    private String album;
    private String genero;
    private String duracao;
    private String ano;
    private String caminho;
    private transient byte[] capa;

    public Musica() { // construtor vazio por causa do Gson conseguir fazer a deserialização do json
    }

    public Musica(String titulo, String artista, String album, String genero, String duracao, String ano, String caminho, byte[] capa) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.genero = genero;
        this.duracao = duracao;
        this.ano = ano;
        this.caminho = caminho;
        this.capa = capa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public byte[] getCapa() {
        return capa;
    }

    public void setCapa(byte[] capa) {
        this.capa = capa;
    }


    /*
    * usando uma sobrescrita de metodo para adaptar o metodo para evitar duplicidade
    * assim comparamos o caminho da musica se passar pela verificao de igualdade entre objetos em memoria */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // verificao em memoria
        if (o == null || getClass() != o.getClass()) return false;
        Musica musica = (Musica) o;
        return java.util.Objects.equals(caminho, musica.caminho);
    }


    /*
    * foi sobrescrevido tambm para o uso interno do java */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(caminho);
    }

    @Override
    public String toString() {
        return "Musica{" +
                "titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", genero='" + genero + '\'' +
                ", duracao='" + duracao + '\'' +
                ", ano='" + ano + '\'' +
                ", caminho='" + caminho + '\'' +
                '}';
    }
}
