package com.davicesar.elevadorAPI.service;

import java.util.*;

import com.davicesar.elevadorAPI.model.*;
import com.davicesar.elevadorAPI.dto.ElevadorDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ElevadorService {
    private final Elevador elevador = new Elevador();
    private final TreeMap<Integer, Set<Direcao>> filaAndares = new TreeMap<>();
    private Direcao direcaoElevador = Direcao.DESCENDO;
    private boolean paradoNoAndar = false;
    private final SimpMessagingTemplate messagingTemplate;
    private ElevadorDTO prevStatus = null;

    public ElevadorService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        new Thread(this::mainLoop).start();
    }

    public void addAndarNaFila(Andar andar) {
        if (andar.numero() > elevador.getAndarMaximo()) {
            throw new IllegalArgumentException("Andar " + andar.numero() + " maior que o máximo permitido: " + elevador.getAndarMaximo());
        }

        if (andar.numero() < elevador.getAndarMinimo()) {
            throw new IllegalArgumentException("Andar " + andar.numero() + " menor que o mínimo permitido: " + elevador.getAndarMinimo());
        }

        Set<Direcao> direcoes = filaAndares.computeIfAbsent(
                andar.numero(),
                k -> new HashSet<>() {
                }
        );

        direcoes.add(andar.direcao());
        sendStatus();
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

        return new ElevadorDTO(
                andarAtual, paradoNoAndar,
                andarMaximo, andarMinimo,
                direcao, filaAndares
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
        Set<Direcao> andaresNaFila = filaAndares.get(andarAtual);

        if (andaresNaFila == null) return;

        if (andaresNaFila.contains(direcao) || andaresNaFila.contains(Direcao.NEUTRO)) {
            andaresNaFila.removeAll(Arrays.asList(Direcao.NEUTRO, direcao));
            paradoNoAndar = true;
            pausarElevador(1500);
            paradoNoAndar = false;
        }

        if (andaresNaFila.isEmpty()) {
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
        sendStatus();
        try { Thread.sleep(tempoMs); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendStatus() {
        if (prevStatus != null && !prevStatus.equals(this.getStatus())) {
            messagingTemplate.convertAndSend("/topic/status", this.getStatus());
        }
        prevStatus = this.getStatus();
    }
}
