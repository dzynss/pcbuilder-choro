package com.pcbuilder.ms_cotizaciones.service;

import com.pcbuilder.ms_cotizaciones.client.ComponenteClient;
import com.pcbuilder.ms_cotizaciones.client.UsuarioClient;
import com.pcbuilder.ms_cotizaciones.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.pcbuilder.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.pcbuilder.ms_cotizaciones.dto.UsuarioResponseDTO;
import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;
import com.pcbuilder.ms_cotizaciones.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_cotizaciones.repository.CotizacionRepository;
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
class CotizacionServiceTest {

    @Mock
    private CotizacionRepository repo;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ComponenteClient componenteClient;

    @InjectMocks
    private CotizacionService service;

    // --- Regla de negocio: cálculo del total (precio del componente x cantidad) ---
    @Test
    void guardar_deberiaCalcularElTotalMultiplicandoPrecioPorCantidad() {
        // Given
        CotizacionRequestDTO dto = new CotizacionRequestDTO(1L, 10L, 3);
        when(usuarioClient.buscarPorId(1L)).thenReturn(new UsuarioResponseDTO(1L, "Kevin", "kevin@pcbuilder.cl", "USER"));
        when(componenteClient.buscarPorId(10L)).thenReturn(
                new ComponenteResponseDTO(10L, "RTX 4070", "Nvidia", 500000.0, 5, "GPU"));
        when(repo.save(any(Cotizacion.class))).thenAnswer(inv -> {
            Cotizacion c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        // When
        CotizacionResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.total()).isEqualTo(1_500_000.0); // 500000 * 3
    }

    @Test
    void guardar_deberiaLanzarExcepcion_cuandoElUsuarioNoExiste() {
        // Given
        CotizacionRequestDTO dto = new CotizacionRequestDTO(404L, 10L, 1);
        Request request = Request.create(Request.HttpMethod.GET, "/api/usuarios/404",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, new RequestTemplate());
        when(usuarioClient.buscarPorId(eq(404L)))
                .thenThrow(new FeignException.NotFound("not found", request, null, null));

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void calcularTotalPorUsuario_deberiaSumarElTotalDeTodasSusCotizaciones() {
        // Given
        Cotizacion c1 = new Cotizacion();
        c1.setTotal(100.0);
        Cotizacion c2 = new Cotizacion();
        c2.setTotal(250.0);
        when(repo.findByIdUsuario(1L)).thenReturn(java.util.List.of(c1, c2));

        // When
        Double total = service.calcularTotalPorUsuario(1L);

        // Then
        assertThat(total).isEqualTo(350.0);
    }
}
