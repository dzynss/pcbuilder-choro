package com.example.presupuesto_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.presupuesto_service.model.Presupuesto;
import com.example.presupuesto_service.dto.PresupuestoDTO;
import com.example.presupuesto_service.service.PresupuestoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class PresupuestoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoService presupuestoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Presupuesto presupuesto;
    private PresupuestoDTO presupuestoDto;

    @BeforeEach
    void setUp() {
        presupuesto = new Presupuesto(
            1L,
            1000,
            200,
            new Date(),
            "APROBADO"
        );

        presupuestoDto = new PresupuestoDTO(
            null,
            1000,
            200,
            new Date(),
            "APROBADO"
        );
        PresupuestoController controller = new PresupuestoController(presupuestoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testObtenerPresupuesto() throws Exception {
        when(presupuestoService.obtenerPorId(1L)).thenReturn(presupuesto);

        mockMvc.perform(get("/presupuestos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAprobado").value(1000))
                .andExpect(jsonPath("$.totalGastado").value(200))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    public void testCrearPresupuesto() throws Exception {
        when(presupuestoService.guardar(any(Presupuesto.class))).thenReturn(presupuesto);

        mockMvc.perform(post("/presupuestos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(presupuestoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAprobado").value(1000))
                .andExpect(jsonPath("$.totalGastado").value(200))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }
}
