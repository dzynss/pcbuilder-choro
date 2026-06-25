package com.pcbuilder.ms_ofertas.service;

import com.pcbuilder.ms_ofertas.dto.OfertaResponseDTO;
import com.pcbuilder.ms_ofertas.entity.Oferta;
import com.pcbuilder.ms_ofertas.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_ofertas.repository.OfertaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfertaServiceTest {

    @Mock
    private OfertaRepository repo;

    @InjectMocks
    private OfertaService service;

    @Test
    void buscarPorCodigo_deberiaRetornarOferta_cuandoElCodigoExiste() {
        // Given
        Oferta oferta = new Oferta();
        oferta.setId(1L);
        oferta.setCodigo("PCGAMER2026");
        oferta.setPorcentajeDescuento(20);
        oferta.setActiva(true);
        when(repo.findByCodigo("PCGAMER2026")).thenReturn(Optional.of(oferta));

        // When
        OfertaResponseDTO resultado = service.buscarPorCodigo("pcgamer2026");

        // Then
        assertThat(resultado.codigo()).isEqualTo("PCGAMER2026");
    }

    @Test
    void buscarPorCodigo_deberiaLanzarExcepcion_cuandoElCodigoNoExiste() {
        // Given
        when(repo.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.buscarPorCodigo("noexiste"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // --- Regla de negocio: aplicación de descuentos ---
    @Test
    void aplicarDescuento_deberiaRestarElPorcentajeCorrecto() {
        // Given
        OfertaResponseDTO oferta = new OfertaResponseDTO(1L, "PCGAMER2026", 20, true);

        // When
        double resultado = service.aplicarDescuento(oferta, 100_000.0);

        // Then
        assertThat(resultado).isEqualTo(80_000.0); // 100.000 - 20%
    }

    @Test
    void aplicarDescuento_deberiaLanzarExcepcion_cuandoLaOfertaNoEstaActiva() {
        // Given
        OfertaResponseDTO ofertaInactiva = new OfertaResponseDTO(2L, "VENCIDO2025", 50, false);

        // When / Then
        assertThatThrownBy(() -> service.aplicarDescuento(ofertaInactiva, 50_000.0))
                .isInstanceOf(IllegalStateException.class);
    }
}
