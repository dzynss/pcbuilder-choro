package com.pcbuilder.ms_resenas.service;

import com.pcbuilder.ms_resenas.client.ComponenteClient;
import com.pcbuilder.ms_resenas.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_resenas.dto.ResenaRequestDTO;
import com.pcbuilder.ms_resenas.dto.ResenaResponseDTO;
import com.pcbuilder.ms_resenas.entity.Resena;
import com.pcbuilder.ms_resenas.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_resenas.repository.ResenaRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository repo;

    @Mock
    private ComponenteClient componenteClient;

    @InjectMocks
    private ResenaService service;

    @Test
    void guardar_deberiaPersistirResena_cuandoElComponenteExiste() {
        // Given
        ResenaRequestDTO dto = new ResenaRequestDTO("Kevin", "Excelente pieza", 5, 10L);
        when(componenteClient.buscarPorId(10L)).thenReturn(
                new ComponenteResponseDTO(10L, "RTX 4070", "Nvidia", 500000.0, 5, "GPU"));
        when(repo.save(any(Resena.class))).thenAnswer(inv -> {
            Resena r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        // When
        ResenaResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.calificacion()).isEqualTo(5);
    }

    @Test
    void guardar_deberiaLanzarExcepcion_cuandoElComponenteNoExisteEnMsComponentes() {
        // Given
        ResenaRequestDTO dto = new ResenaRequestDTO("Kevin", "Esto no existe", 1, 999L);
        Request request = Request.create(Request.HttpMethod.GET, "/api/componentes/999",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, new RequestTemplate());
        when(componenteClient.buscarPorId(eq(999L)))
                .thenThrow(new FeignException.NotFound("not found", request, null, null));

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
