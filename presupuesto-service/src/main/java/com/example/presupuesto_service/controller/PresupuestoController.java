package com.example.presupuesto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.presupuesto_service.dto.PresupuestoDTO;
import com.example.presupuesto_service.model.Presupuesto;
import com.example.presupuesto_service.service.PresupuestoService;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;
    private static final Logger logger = LoggerFactory.getLogger(PresupuestoController.class);

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @PostMapping
    public ResponseEntity<PresupuestoDTO> crearPresupuesto(@RequestBody PresupuestoDTO presupuestoDto) {
        Presupuesto nuevoPresupuesto = presupuestoService.guardar(presupuestoDto.toModel());
        return ResponseEntity.ok(PresupuestoDTO.fromModel(nuevoPresupuesto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDTO> obtenerPresupuesto(@PathVariable Long id) {
        logger.info("GET /presupuestos/{} - Obteniendo presupuesto", id);
        try {
            Presupuesto presupuesto = presupuestoService.obtenerPorId(id);
            logger.info("Presupuesto obtenido id={}", id);
            return ResponseEntity.ok(PresupuestoDTO.fromModel(presupuesto));
        } catch (Exception e) {
            logger.error("Error al obtener presupuesto id={}: {}", id, e.getMessage());
            throw e;
        }
    }    
}