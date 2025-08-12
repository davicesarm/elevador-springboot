package com.davicesar.elevadorAPI.service;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Arrays;
import com.davicesar.elevadorAPI.model.*;
import com.davicesar.elevadorAPI.dto.ElevadorDTO;
import org.springframework.stereotype.Service;

@Service
public class ElevadorService {
    private final Elevador elevador;
    private final TreeMap<Integer, Boolean[]> filaAndares;
    private Direcao direcaoElevador;

    public ElevadorService() {
        this.elevador = new Elevador();
        this.filaAndares = new TreeMap<>();
        this.direcaoElevador = Direcao.DESCENDO;
        new Thread(this::mainLoop).start();
    }

    public void addAndarNaFila(Andar andar) {
        if (andar.numero() > elevador.getAndarMaximo()) {
            throw new IllegalArgumentException("Andar " + andar.numero() + " maior que o máximo permitido: " + elevador.getAndarMaximo());
        }

        if (andar.numero() < elevador.getAndarMinimo()) {
            throw new IllegalArgumentException("Andar " + andar.numero() + " menor que o mínimo permitido: " + elevador.getAndarMinimo());
        }

        Boolean[] direcoes = filaAndares.computeIfAbsent(
                andar.numero(),
                k -> {
                    Boolean[] novoArray = new Boolean[3];
                    Arrays.fill(novoArray, false);
                    return novoArray;
                }
        );

        direcoes[andar.direcao().getValor()] = true;
    }

    public void reiniciar() {
        filaAndares.clear();
        elevador.reiniciar();
    }

    public ElevadorDTO getStatus() {
        int andarAtual = elevador.getAndarAtual();
        int andarMaximo = elevador.getAndarMaximo();
        int andarMinimo = elevador.getAndarMinimo();
        Direcao direcao = this.direcaoElevador;
        ArrayList<Integer> andaresApertados = new ArrayList<>(filaAndares.keySet());

        return new ElevadorDTO(
                andarAtual, andarMaximo, andarMinimo, direcao, andaresApertados, filaAndares
        );
    }

    private void mainLoop() {
        while (true) {
            int andarAtual = elevador.getAndarAtual();

            if (filaAndares.isEmpty() && direcaoElevador != Direcao.NEUTRO) {
                direcaoElevador = Direcao.NEUTRO;
            } else if (!filaAndares.isEmpty() && direcaoElevador == Direcao.NEUTRO) {
                direcaoElevador = Direcao.SUBINDO;
            }

            switch (direcaoElevador) {
                case SUBINDO -> processarMovimentoSubindo(andarAtual);
                case DESCENDO -> processarMovimentoDescendo(andarAtual);
                default -> elevador.descer();
            }

            pausarElevador(800);
        }
    }

    private void processarParada(Direcao direcao) {
        int andarAtual = elevador.getAndarAtual();
        Boolean[] andaresNaFila = filaAndares.get(andarAtual);

        if (andaresNaFila == null) return;

        int neutroIndice = Direcao.NEUTRO.getValor();
        int direcaoIndice = direcao.getValor();

        if (andaresNaFila[direcaoIndice] || andaresNaFila[neutroIndice]) {
            andaresNaFila[direcaoIndice] = false;
            andaresNaFila[neutroIndice] = false;
            pausarElevador(1500);
        }

        if (Arrays.stream(andaresNaFila).noneMatch(valor -> valor)) {
            filaAndares.remove(andarAtual);
        }
    }

    private void processarMovimentoSubindo(int andarAtual) {
        processarParada(Direcao.SUBINDO);
        if (!filaAndares.isEmpty()) {
            if (andarAtual >= filaAndares.lastKey()) {
                direcaoElevador = Direcao.DESCENDO;
            } else{
                elevador.subir();
            }
        }
    }

    private void processarMovimentoDescendo(int andarAtual){
        processarParada(Direcao.DESCENDO);
        if (!filaAndares.isEmpty()) {
            if (andarAtual <= filaAndares.firstKey()) {
                direcaoElevador = Direcao.SUBINDO;
            } else {
                elevador.descer();
            }
        }
    }

    private void pausarElevador(int tempoMs) {
        try { Thread.sleep(tempoMs); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
