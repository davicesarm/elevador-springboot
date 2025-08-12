package com.davicesar.elevadorAPI.model;

public record Andar(int numero, Direcao direcao) {
    public Andar {
        if (direcao == null) direcao = Direcao.NEUTRO;
    }
}