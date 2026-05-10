package com.otaviogustavo;

public class Musica {

    private String titulo;
    private String artista;
    private String duracao;
    private String caminho;

    public Musica(String titulo, String artista, String duracao, String caminho) {
        this.titulo = titulo;
        this.artista = artista;
        this.duracao = duracao;
        this.caminho = caminho;
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

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }


    /*
    * usando uma sobrescrita de metodo para adaptar o metodo para evitar duplicidade
    * assim comparamos o caminho da musica se passar pela verificação de igualdade entre objetos em memoria */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // verificação em memoria
        if (o == null || getClass() != o.getClass()) return false;
        Musica musica = (Musica) o;
        return java.util.Objects.equals(caminho, musica.caminho);
    }


    /*
    * foi sobrescrevido também para o uso interno do java */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(caminho);
    }

    /*

    Explicação do funcionamento interno do java em estruturas como LinkedHashSet que estamos usando.

    * Tentou adicionar uma Música no Set
          ?
          ?
1. Chama o 'hashCode()' da música para calcular a "gaveta" (número hash).
          ?
          ??? Gaveta está VAZIA? ??? Guarda a música lá. (Fim! O 'equals' nem foi chamado).
          ?
          ??? Gaveta está OCUPADA? (Colisão de hash)
                    ?
                    ?
               2. O Set chama o 'equals()' para comparar a música nova
                  com a(s) música(s) que já estão dentro daquela gaveta.
                    ?
                    ??? Retornou TRUE (Caminhos iguais) ??? Rejeita (Música duplicada).
                    ??? Retornou FALSE (Caminhos diferentes) ??? Adiciona na mesma gaveta.*/
}
