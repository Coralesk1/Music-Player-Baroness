package com.otaviogustavo.commands;

import com.otaviogustavo.controllers.MainController;

public class ComandoProximo implements Comando {
    private MainController receiver;

    public ComandoProximo(MainController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executar() {
        receiver.tocarProximaMusica();
    }
}
