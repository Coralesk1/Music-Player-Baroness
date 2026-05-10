package com.otaviogustavo;

import java.util.*;

public class GerenciadorEstruturas {

    private Set<Musica> bibliotecaGeral;
    private Map<String, List<Musica>> playlists;

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
}
