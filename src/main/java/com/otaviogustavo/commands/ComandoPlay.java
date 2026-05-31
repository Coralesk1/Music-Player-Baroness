package com.otaviogustavo.commands;

import com.otaviogustavo.controllers.MainController;

public class ComandoPlay implements Comando {
    private MainController receiver;

    public ComandoPlay(MainController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executar() {
        receiver.alternarReproducao();
    }
}
