package com.otaviogustavo.commands;

import com.otaviogustavo.controllers.MainController;

public class ComandoAnterior implements Comando {
    private MainController receiver;

    public ComandoAnterior(MainController receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executar() {
        receiver.tocarMusicaAnterior();
    }
}
