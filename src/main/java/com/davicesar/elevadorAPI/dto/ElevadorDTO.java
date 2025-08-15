package com.davicesar.elevadorAPI.dto;

import com.davicesar.elevadorAPI.model.Direcao;

import java.util.Set;
import java.util.TreeMap;

public record ElevadorDTO(
        int andarAtual,
        boolean paradoNoAndar,
        int andarMaximo,
        int andarMinimo,
        Direcao direcao,
        TreeMap<Integer, Set<Direcao>> andaresApertados
) {}
