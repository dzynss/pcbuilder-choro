package com.pcbuilder.ms_componentes.service;

import com.pcbuilder.ms_componentes.dto.ComponenteRequestDTO;
import com.pcbuilder.ms_componentes.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_componentes.entity.Categoria;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_componentes.repository.CategoriaRepository;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponenteServiceTest {

    @Mock
    private ComponenteRepository repo;

    @Mock
    private CategoriaRepository categoriaRepo;

    @InjectMocks
    private ComponenteService service;

    @Test
    void buscarPorId_deberiaRetornarComponente_cuandoExiste() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setId(10L);
        categoria.setNombre("GPU");

        Componente componente = new Componente();
        componente.setId(10L);
        componente.setNombre("RTX 4080");
        componente.setMarca("Nvidia");
        componente.setPrecio(500000.0);
        componente.setStock(5);
        componente.setCategoria(categoria);

        when(repo.findById(10L)).thenReturn(Optional.of(componente));

        // When
        ComponenteResponseDTO resultado = service.buscarPorId(10L);

        // Then
        assertThat(resultado.nombre()).isEqualTo("RTX 4080");
        assertThat(resultado.categoria()).isEqualTo("GPU");
    }

    @Test
    void buscarPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        // Given
        when(repo.findById(404L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.buscarPorId(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void guardar_deberiaLanzarExcepcion_cuandoLaCategoriaNoExiste() {
        // Given
        ComponenteRequestDTO dto = new ComponenteRequestDTO("RAM 16GB", "Kingston", 45000.0, 10, 99L);
        when(categoriaRepo.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void guardar_deberiaPersistirComponente_cuandoLaCategoriaExiste() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setId(8L);
        categoria.setNombre("RAM");
        ComponenteRequestDTO dto = new ComponenteRequestDTO("RAM 16GB", "Kingston", 45000.0, 10, 2L);

        when(categoriaRepo.findById(2L)).thenReturn(Optional.of(categoria));
        when(repo.save(any(Componente.class))).thenAnswer(inv -> {
            Componente c = inv.getArgument(0);
            c.setId(8L);
            return c;
        });

        // When
        ComponenteResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.id()).isEqualTo(8L);
        assertThat(resultado.categoria()).isEqualTo("RAM");
    }
}
