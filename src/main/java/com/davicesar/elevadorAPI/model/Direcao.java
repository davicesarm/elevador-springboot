package com.davicesar.elevadorAPI.model;

public enum Direcao {
    SUBINDO(0),
    DESCENDO(1),
    NEUTRO(2);

    private final int valor;
    Direcao(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return this.valor;
    }
}
