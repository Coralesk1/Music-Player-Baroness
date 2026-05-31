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

    public void setHistoricoReproducao(Stack<Musica> historico) {
        this.historicoReproducao = historico;
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

    public List<Musica> buscarMusicaNaBiblioteca(String termo) {
        if (termo == null || termo.isEmpty()) {
            return new ArrayList<>(bibliotecaGeral);
        }

        String filtro = termo.toLowerCase();
        List<Musica> resultado = new ArrayList<>();

        for (Musica musica : bibliotecaGeral) {
            if (musica.getTitulo().toLowerCase().contains(filtro) ||
                musica.getArtista().toLowerCase().contains(filtro)) {
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




