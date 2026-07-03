package com.example.presupuesto_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.presupuesto_service.model.Presupuesto;
import com.example.presupuesto_service.repository.PresupuestoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PresupuestoServiceTest {
    private PresupuestoService presupuestoService;
    @Mock
    private PresupuestoRepository presupuestoRepository;

    @BeforeEach
    void setUp() {
        presupuestoService = new PresupuestoService(presupuestoRepository);
    }

    @Test
    void testGuardar() {
        Presupuesto presupuesto = new Presupuesto(1L, 1000, 200, new Date(), "APROBADO");
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto);
        Presupuesto resultado = presupuestoService.guardar(presupuesto);
        assertNotNull(resultado);
        assertEquals(1000, resultado.getTotalAprobado());
        assertEquals("APROBADO", resultado.getEstado());
        verify(presupuestoRepository).save(any(Presupuesto.class));
    }

    @Test
    void testObtenerPorId() {
        Long id = 1L;
        Presupuesto presupuesto = new Presupuesto(id, 750, 150, new Date(), "REVISADO");
        when(presupuestoRepository.findById(id)).thenReturn(Optional.of(presupuesto));
        Presupuesto resultado = presupuestoService.obtenerPorId(id);
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(presupuestoRepository).findById(id);
    }
}
