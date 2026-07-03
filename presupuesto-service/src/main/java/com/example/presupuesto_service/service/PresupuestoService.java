package com.example.presupuesto_service.service;

import org.springframework.stereotype.Service;

import com.example.presupuesto_service.model.Presupuesto;
import com.example.presupuesto_service.repository.PresupuestoRepository;
import com.example.presupuesto_service.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private static final Logger logger = LoggerFactory.getLogger(PresupuestoService.class);

    public PresupuestoService(PresupuestoRepository presupuestoRepository) {
        this.presupuestoRepository = presupuestoRepository;
    }
    public Presupuesto guardar(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    public Presupuesto obtenerPorId(Long id) {
        logger.info("Buscando presupuesto por id={}", id);
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Presupuesto no encontrado id={}", id);
                    return new ResourceNotFoundException("Presupuesto no existe");
                });
        logger.info("Presupuesto encontrado id={}", id);
        return presupuesto;
    }    
}