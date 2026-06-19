package com.pcbuilder.ms_cotizaciones.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;
import com.pcbuilder.ms_cotizaciones.service.CotizacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
@Slf4j // habilitando el logging pal profe
public class CotizacionController {

    private final CotizacionService service;

    // endpoint 1: busqueda por atributo (Por Usuario)
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Cotizacion>> buscarPorUsuario(@PathVariable Long idUsuario) {
        log.info("Buscando todas las compras del loco ID: {}", idUsuario);
        return ResponseEntity.ok(service.buscarPorUsuario(idUsuario));
    }

    // endpoint 2: totales
    @GetMapping("/usuario/{idUsuario}/total")
    public ResponseEntity<String> totalPorUsuario(@PathVariable Long idUsuario) {
        log.info("Calculando el medio vuelto del loco ID: {}", idUsuario);
        Double total = service.calcularTotalPorUsuario(idUsuario);
        return ResponseEntity.ok("El compa ha gastado un total de: $" + total);
    }

    // el sucio crud
    @GetMapping
    public ResponseEntity<List<Cotizacion>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> buscarUno(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cotizacion c) {
        log.info("Armando una nueva cotización pal usuario {} con la pieza {}", c.getIdUsuario(), c.getIdComponente());
        try {
            return new ResponseEntity<>(service.guardar(c), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Condoro en la comunicación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cotizacion c) {
        log.warn("Actualizando la cotización ID: {}", id);
        Cotizacion existe = service.buscarPorId(id);
        existe.setIdUsuario(c.getIdUsuario());
        existe.setIdComponente(c.getIdComponente());
        existe.setCantidad(c.getCantidad());
        try {
            return ResponseEntity.ok(service.guardar(existe));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.error("Haciendo desaparecer la cotización ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    // tu mama
}