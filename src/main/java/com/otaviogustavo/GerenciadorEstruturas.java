package com.otaviogustavo;

import java.util.*;

public class GerenciadorEstruturas {

    private Set<Musica> bibliotecaGeral;
    private Map<PlayList, List<Musica>> playlists;

    public GerenciadorEstruturas(){
        this.bibliotecaGeral = new LinkedHashSet<>();
        this.playlists = new HashMap<>();
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

    public void setPlaylists(Map.Entry<PlayList, List<Musica>> playlist) {
        playlists.put(playlist.getKey(), playlist.getValue());
    }

    public void criaPlaylistVazia(PlayList playList){
        playlists.put(playList, new ArrayList<>());
    }

    public void adicionarMusicaPLaylist(PlayList playList, Musica musica){
        playlists.get(playList).add(musica);
    }


}




