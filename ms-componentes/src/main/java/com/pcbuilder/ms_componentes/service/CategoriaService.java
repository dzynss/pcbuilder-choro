package com.pcbuilder.ms_componentes.service;

import com.pcbuilder.ms_componentes.dto.CategoriaRequestDTO;
import com.pcbuilder.ms_componentes.dto.CategoriaResponseDTO;
import com.pcbuilder.ms_componentes.entity.Categoria;
import com.pcbuilder.ms_componentes.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_componentes.repository.CategoriaRepository;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio de categorías del catálogo. Usada por {@link com.pcbuilder.ms_componentes.controller.CategoriaController}.
 * Convierte entre {@link Categoria} y los DTOs de request/response, y usa
 * {@link ComponenteRepository} para validar que no queden componentes huérfanos al eliminar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository repo;
    private final ComponenteRepository componenteRepo;

    /** Devuelve todas las categorías del catálogo como DTOs de respuesta. */
    public List<CategoriaResponseDTO> listarTodas() {
        log.info("Listando todas las categorías");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca una categoría por ID; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public CategoriaResponseDTO buscarPorId(Long id) {
        log.info("Buscando la categoría con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /** Crea y persiste una nueva categoría a partir del DTO de request. */
    public CategoriaResponseDTO guardar(CategoriaRequestDTO dto) {
        log.info("Creando una nueva categoría: {}", dto.nombre());
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        return aResponseDTO(repo.save(categoria));
    }

    /** Actualiza el nombre de una categoría existente; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        log.info("Actualizando la categoría con ID: {}", id);
        Categoria existente = buscarEntidadPorId(id);
        existente.setNombre(dto.nombre());
        return aResponseDTO(repo.save(existente));
    }

    /**
     * Elimina una categoría por ID. Lanza {@link RecursoNoEncontradoException} (404) si no
     * existe, o {@link DataIntegrityViolationException} (409, vía GlobalExceptionHandler) si
     * todavía tiene componentes asociados.
     */
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            log.warn("Se intentó eliminar la categoría con ID {} pero no existe", id);
            throw new RecursoNoEncontradoException("La categoría con ID " + id + " no existe.");
        }
        // Se impide eliminar una categoría que todavía tiene componentes asociados,
        // ya que ComponenteRepository expone existsByCategoriaId (query derivada trivial,
        // no requiere cambios de esquema/Liquibase) para validar la FK antes del borrado.
        if (componenteRepo.existsByCategoriaId(id)) {
            log.warn("No se puede eliminar la categoría con ID {} porque tiene componentes asociados", id);
            throw new DataIntegrityViolationException(
                    "No se puede eliminar la categoría con ID " + id + " porque tiene componentes asociados.");
        }
        log.info("Eliminando la categoría con ID: {}", id);
        repo.deleteById(id);
    }

    /** Busca la entity por ID o lanza {@link RecursoNoEncontradoException}; usado internamente por buscar/actualizar/eliminar. */
    private Categoria buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("La categoría con ID {} no existe", id);
                    return new RecursoNoEncontradoException("La categoría con ID " + id + " no existe.");
                });
    }

    /** Mapea la entity {@link Categoria} al DTO de respuesta expuesto por el controller. */
    private CategoriaResponseDTO aResponseDTO(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNombre());
    }
}
