package com.otaviogustavo;

public class Musica {

    private String titulo;
    private String artista;
    private String duracao;
    private String caminho;

    public Musica() { // construtor vazio por causa do Gson conseguir fazer a deserialização do json
    }

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
    * assim comparamos o caminho da musica se passar pela verifica��o de igualdade entre objetos em memoria */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // verifica��o em memoria
        if (o == null || getClass() != o.getClass()) return false;
        Musica musica = (Musica) o;
        return java.util.Objects.equals(caminho, musica.caminho);
    }


    /*
    * foi sobrescrevido tamb�m para o uso interno do java */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(caminho);
    }

    @Override
    public String toString() {
        return "Musica{" +
                "titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", duracao='" + duracao + '\'' +
                ", caminho='" + caminho + '\'' +
                '}';
    }

    /*

    Explica��o do funcionamento interno do java em estruturas como LinkedHashSet que estamos usando.

    * Tentou adicionar uma M�sica no Set
          ?
          ?
1. Chama o 'hashCode()' da m�sica para calcular a "gaveta" (n�mero hash).
          ?
          ??? Gaveta est� VAZIA? ??? Guarda a m�sica l�. (Fim! O 'equals' nem foi chamado).
          ?
          ??? Gaveta est� OCUPADA? (Colis�o de hash)
                    ?
                    ?
               2. O Set chama o 'equals()' para comparar a m�sica nova
                  com a(s) m�sica(s) que j� est�o dentro daquela gaveta.
                    ?
                    ??? Retornou TRUE (Caminhos iguais) ??? Rejeita (M�sica duplicada).
                    ??? Retornou FALSE (Caminhos diferentes) ??? Adiciona na mesma gaveta.*/
}
