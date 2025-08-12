package com.davicesar.elevadorAPI.controller;

import com.davicesar.elevadorAPI.dto.ElevadorDTO;
import com.davicesar.elevadorAPI.model.Andar;
import com.davicesar.elevadorAPI.service.ElevadorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ElevadorController {
    private final ElevadorService elevadorService;

    public ElevadorController () {
        this.elevadorService = new ElevadorService();
    }

    @GetMapping("/status")
    public ElevadorDTO status() {
        return elevadorService.getStatus();
    }

    @GetMapping("/reiniciar")
    public void reiniciar() {
        elevadorService.reiniciar();
    }

    @PostMapping("/addAndar")
    public void adicionarAndar(@RequestBody Andar andar) {
        elevadorService.addAndarNaFila(andar);
    }

}
