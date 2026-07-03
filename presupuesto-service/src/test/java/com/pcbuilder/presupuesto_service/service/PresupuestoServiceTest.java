package com.pcbuilder.presupuesto_service.service;

import com.pcbuilder.presupuesto_service.dto.PresupuestoRequestDTO;
import com.pcbuilder.presupuesto_service.dto.PresupuestoResponseDTO;
import com.pcbuilder.presupuesto_service.entity.Presupuesto;
import com.pcbuilder.presupuesto_service.repository.PresupuestoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoServiceTest {

    @Mock
    private PresupuestoRepository repo;

    @InjectMocks
    private PresupuestoService service;

    @Test
    void listarTodos_deberiaRetornarTodosLosPresupuestos() {
        // Given
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1L);
        presupuesto.setTotalAprobado(100_000);
        presupuesto.setTotalGastado(25_000);
        presupuesto.setFechaRegistro(LocalDateTime.now());
        presupuesto.setEstado("APROBADO");
        when(repo.findAll()).thenReturn(List.of(presupuesto));

        // When
        List<PresupuestoResponseDTO> resultado = service.listarTodos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estado()).isEqualTo("APROBADO");
    }

    @Test
    void guardar_deberiaCrearYRetornarElPresupuesto() {
        // Given
        LocalDateTime fecha = LocalDateTime.now();
        PresupuestoRequestDTO dto = new PresupuestoRequestDTO(1000, 200, fecha, "APROBADO");

        Presupuesto guardado = new Presupuesto();
        guardado.setId(1L);
        guardado.setTotalAprobado(1000);
        guardado.setTotalGastado(200);
        guardado.setFechaRegistro(fecha);
        guardado.setEstado("APROBADO");
        when(repo.save(any(Presupuesto.class))).thenReturn(guardado);

        // When
        PresupuestoResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.totalAprobado()).isEqualTo(1000);
        assertThat(resultado.estado()).isEqualTo("APROBADO");
    }
}
