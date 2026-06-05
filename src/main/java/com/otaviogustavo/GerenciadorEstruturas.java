package com.otaviogustavo;

import java.util.*;

public class GerenciadorEstruturas {

    private Set<Musica> bibliotecaGeral;
    private Map<PlayList, List<Musica>> playlists;
    private Stack<Musica> historicoReproducao;

    public GerenciadorEstruturas(){
        this.bibliotecaGeral = new LinkedHashSet<>();
        this.playlists = new HashMap<>();
        this.historicoReproducao = new Stack<>();
    }

    public Stack<Musica> getHistoricoReproducao() {
        return historicoReproducao;
    }

    public void adicionarMusicaHistorico(Musica musica) {
        if (musica == null) return;

        historicoReproducao.remove(musica);
        historicoReproducao.push(musica);

        while (historicoReproducao.size() > 20) {
            historicoReproducao.remove(0);
        }
    }

    public void adicionarMusicaBiblioteca(Musica musica){
        bibliotecaGeral.add(musica);
    }

    public Set<Musica> getBibliotecaGeral() {
        return bibliotecaGeral;
    }

    public Map<PlayList, List<Musica>> getPlaylists(){
        return playlists;
    }

    public void adicionaPlaylistVazia(PlayList playList){
        playlists.put(playList, new ArrayList<>());
    }

    public void adicionarMusicaPLaylist(PlayList playList, Musica musica){
        playlists.get(playList).add(musica);
    }

    public void removerMusicaPlaylist(PlayList playList, Musica musica) {
        if (playlists.containsKey(playList)) {
            playlists.get(playList).remove(musica);
        }
    }

    public void removerMusicaBiblioteca(Musica musica) {
        bibliotecaGeral.remove(musica);
    }

    public void removerPlaylist(String nome) {
        playlists.keySet().removeIf(playlist -> playlist.getNome().equals(nome));
    }

    public List<Musica> buscarMusicaNaPlaylist(PlayList playlist, String termo, String genero, String artista, String album) {

        List<Musica> resultado = new ArrayList<>();
        List<Musica> musicasDaPlaylist = playlists.get(playlist);

        if (musicasDaPlaylist == null) return resultado;

        String filtroTermo = (termo == null) ? "" : termo.toLowerCase();

        for (Musica musica : musicasDaPlaylist) {
            boolean matchesTermo = filtroTermo.isEmpty() ||
                    musica.getTitulo().toLowerCase().contains(filtroTermo) ||
                    musica.getArtista().toLowerCase().contains(filtroTermo);

            boolean matchesGenre = (genero == null || genero.equals("Todos os gêneros")) ||
                    (musica.getGenero() != null && musica.getGenero().equalsIgnoreCase(genero));

            boolean matchesArtist = (artista == null || artista.equals("Todos os artistas")) ||
                    (musica.getArtista() != null && musica.getArtista().equalsIgnoreCase(artista));

            boolean matchesAlbum = (album == null || album.equals("Todos os álbuns")) ||
                    (musica.getAlbum() != null && musica.getAlbum().equalsIgnoreCase(album));

            if (matchesTermo && matchesGenre && matchesArtist && matchesAlbum) {
                resultado.add(musica);
            }
        }
        return resultado;
    }

    public List<Musica> buscarMusicaNaBiblioteca(String termo, String genero, String artista, String album) {

        List<Musica> resultado = new ArrayList<>();
        String filtroTermo = (termo == null) ? "" : termo.toLowerCase();

        for (Musica musica : bibliotecaGeral) {
            boolean matchesTermo = filtroTermo.isEmpty() ||
                    musica.getTitulo().toLowerCase().contains(filtroTermo) ||
                    musica.getArtista().toLowerCase().contains(filtroTermo);

            boolean matchesGenre = (genero == null || genero.equals("Todos os gêneros")) ||
                    (musica.getGenero() != null && musica.getGenero().equalsIgnoreCase(genero));

            boolean matchesArtist = (artista == null || artista.equals("Todos os artistas")) ||
                    (musica.getArtista() != null && musica.getArtista().equalsIgnoreCase(artista));

            boolean matchesAlbum = (album == null || album.equals("Todos os álbuns")) ||
                    (musica.getAlbum() != null && musica.getAlbum().equalsIgnoreCase(album));

            if (matchesTermo && matchesGenre && matchesArtist && matchesAlbum) {
                resultado.add(musica);
            }
        }
        return resultado;
    }

    public void ordenarLista(List<Musica> lista, String criterio) {
        if (criterio == null) return;

        switch (criterio) {
            case "Ordenar por nome":
                lista.sort((m1, m2) -> m1.getTitulo().compareToIgnoreCase(m2.getTitulo()));
                break;
            case "Ordenar por artista":
                lista.sort((m1, m2) -> m1.getArtista().compareToIgnoreCase(m2.getArtista()));
                break;
        }
    }

}




