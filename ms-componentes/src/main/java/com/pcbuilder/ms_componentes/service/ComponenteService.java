package com.pcbuilder.ms_componentes.service;

import com.pcbuilder.ms_componentes.dto.ComponenteRequestDTO;
import com.pcbuilder.ms_componentes.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_componentes.entity.Categoria;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_componentes.repository.CategoriaRepository;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio del catálogo de componentes. Usada por
 * {@link com.pcbuilder.ms_componentes.controller.ComponenteController}. Convierte entre
 * {@link Componente} y los DTOs de request/response; los datos de precio/stock que expone
 * (ComponenteResponseDTO) son consumidos vía Feign por ms_cotizaciones (para el total real
 * de una cotización), ms-resenas y ms-soporte.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComponenteService {

    private final ComponenteRepository repo;
    private final CategoriaRepository categoriaRepo;

    /** Devuelve todos los componentes del catálogo como DTOs de respuesta. */
    public List<ComponenteResponseDTO> buscarTodos() {
        log.info("Listando todos los componentes");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca un componente por ID; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public ComponenteResponseDTO buscarPorId(Long id) {
        log.info("Buscando el componente con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /** Crea y persiste un nuevo componente, resolviendo su categoría por ID (mapearDatos). */
    public ComponenteResponseDTO guardar(ComponenteRequestDTO dto) {
        log.info("Creando un nuevo componente: {}", dto.nombre());
        Componente c = new Componente();
        mapearDatos(c, dto);
        return aResponseDTO(repo.save(c));
    }

    /** Actualiza un componente existente; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public ComponenteResponseDTO actualizar(Long id, ComponenteRequestDTO dto) {
        log.info("Actualizando el componente con ID: {}", id);
        Componente existente = buscarEntidadPorId(id);
        mapearDatos(existente, dto);
        return aResponseDTO(repo.save(existente));
    }

    /** Elimina un componente por ID; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            log.warn("Se intentó eliminar el componente con ID {} pero no existe", id);
            throw new RecursoNoEncontradoException("El componente con ID " + id + " no existe.");
        }
        log.info("Eliminando el componente con ID: {}", id);
        repo.deleteById(id);
    }

    /**
     * Copia los campos del DTO a la entity y resuelve la {@link Categoria} por su ID
     * (idCategoria); lanza {@link RecursoNoEncontradoException} (404) si la categoría no existe.
     */
    private void mapearDatos(Componente c, ComponenteRequestDTO dto) {
        Categoria categoria = categoriaRepo.findById(dto.idCategoria())
                .orElseThrow(() -> {
                    log.error("La categoría con ID {} no existe", dto.idCategoria());
                    return new RecursoNoEncontradoException("La categoría con ID " + dto.idCategoria() + " no existe.");
                });
        c.setNombre(dto.nombre());
        c.setMarca(dto.marca());
        c.setPrecio(dto.precio());
        c.setStock(dto.stock());
        c.setCategoria(categoria);
    }

    /** Busca la entity por ID o lanza {@link RecursoNoEncontradoException}; usado internamente por buscar/actualizar/eliminar. */
    private Componente buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("El componente con ID {} no existe", id);
                    return new RecursoNoEncontradoException("El componente con ID " + id + " no existe.");
                });
    }

    /** Mapea la entity {@link Componente} al DTO de respuesta expuesto por el controller y consumido vía Feign por otros servicios. */
    private ComponenteResponseDTO aResponseDTO(Componente c) {
        String nombreCategoria = c.getCategoria() != null ? c.getCategoria().getNombre() : null;
        return new ComponenteResponseDTO(c.getId(), c.getNombre(), c.getMarca(), c.getPrecio(), c.getStock(), nombreCategoria);
    }
}
