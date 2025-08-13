package com.davicesar.elevadorAPI.dto;

import com.davicesar.elevadorAPI.model.Direcao;
import java.util.ArrayList;
// import java.util.TreeMap;

public record ElevadorDTO(
        int andarAtual,
        boolean paradoNoAndar,
        int andarMaximo,
        int andarMinimo,
        Direcao direcao,
        ArrayList<Integer> andaresApertados,
        // TreeMap<Integer, Boolean[]> filaAndares,
        long tempoRestantePausaMs
) {}
