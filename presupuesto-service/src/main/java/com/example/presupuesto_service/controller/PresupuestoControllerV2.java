package com.example.presupuesto_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.presupuesto_service.assemblers.PresupuestoModelAssembler;
import com.example.presupuesto_service.model.Presupuesto;
import com.example.presupuesto_service.service.PresupuestoService;

@RestController
@RequestMapping("/presupuestos/v2")
public class PresupuestoControllerV2 {
    private final PresupuestoService presupuestoService;
    private final PresupuestoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PresupuestoControllerV2.class);

    public PresupuestoControllerV2(PresupuestoService presupuestoService,PresupuestoModelAssembler assembler) {
        this.presupuestoService = presupuestoService;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public EntityModel<Presupuesto> obtenerPresupuesto(@PathVariable Long id) {
        logger.info("V2 GET /presupuestos/{} - Obteniendo presupuesto", id);
        Presupuesto p = presupuestoService.obtenerPorId(id);
        return assembler.toModel(p);
    }
}
