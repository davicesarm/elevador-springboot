package com.davicesar.elevadorAPI.model;

public class Elevador {
    private int andarAtual;
    private int andarMaximo;
    private int andarMinimo;

    public Elevador() {
        andarMaximo = 30;
        andarMinimo = 0;
    };

    public Elevador(int andarMinimo, int andarMaximo) {
        this.andarMaximo = andarMaximo;
        this.andarMinimo = andarMinimo;
        andarAtual = andarMinimo;
    }

    public void reiniciar() {
        andarAtual = andarMinimo;
    }

    public int getAndarAtual () {
        return andarAtual;
    }

    public int getAndarMinimo() {
        return andarMinimo;
    }

    public void setAndarMinimo(int andarMinimo) {
        this.andarMinimo = andarMinimo;
    }

    public int getAndarMaximo() {
        return andarMaximo;
    }

    public void setAndarMaximo(int andarMaximo) {
        this.andarMaximo = andarMaximo;
    }

    public void subir() {
        if (andarAtual < andarMaximo) {
            andarAtual++;
        }
    }

    public void descer() {
        if (andarAtual > andarMinimo) {
            andarAtual--;
        }
    }

}
