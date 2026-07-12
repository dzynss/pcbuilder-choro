package com.pcbuilder.ms_resenas.service;

import com.pcbuilder.ms_resenas.client.ComponenteClient;
import com.pcbuilder.ms_resenas.dto.ResenaRequestDTO;
import com.pcbuilder.ms_resenas.dto.ResenaResponseDTO;
import com.pcbuilder.ms_resenas.entity.Resena;
import com.pcbuilder.ms_resenas.exception.ErrorComunicacionException;
import com.pcbuilder.ms_resenas.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_resenas.repository.ResenaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio de reseñas: CRUD sobre {@link ResenaRepository} y validación de integridad referencial
 * contra ms-componentes a través de {@link ComponenteClient} (Feign). Usada exclusivamente por {@code ResenaController}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResenaService {

    private final ResenaRepository repo;
    private final ComponenteClient componenteClient;

    /** Retorna todas las reseñas mapeadas a DTO. */
    public List<ResenaResponseDTO> buscarTodos() {
        log.info("Buscando todas las reseñas");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca una reseña por ID; lanza {@link RecursoNoEncontradoException} (404) si no existe. */
    public ResenaResponseDTO buscarPorId(Long id) {
        log.info("Buscando reseña con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /** Filtra reseñas por calificación (estrellas) usando {@code ResenaRepository#findByCalificacion}. */
    public List<ResenaResponseDTO> buscarPorEstrellas(Integer calificacion) {
        log.info("Buscando reseñas con calificación: {}", calificacion);
        return repo.findByCalificacion(calificacion).stream().map(this::aResponseDTO).toList();
    }

    /** Crea una reseña; primero valida el componente contra ms-componentes, luego persiste la entidad nueva. */
    public ResenaResponseDTO guardar(ResenaRequestDTO dto) {
        log.info("Creando reseña para el componente ID: {}", dto.idComponente());
        validarComponenteExiste(dto.idComponente());
        Resena r = new Resena();
        mapearDatos(r, dto);
        ResenaResponseDTO creada = aResponseDTO(repo.save(r));
        log.info("Reseña creada con ID: {}", creada.id());
        return creada;
    }

    /** Actualiza una reseña existente; revalida el componente en ms-componentes antes de sobrescribir los datos. */
    public ResenaResponseDTO actualizar(Long id, ResenaRequestDTO dto) {
        log.info("Actualizando reseña con ID: {}", id);
        validarComponenteExiste(dto.idComponente());
        Resena existente = buscarEntidadPorId(id);
        mapearDatos(existente, dto);
        ResenaResponseDTO actualizada = aResponseDTO(repo.save(existente));
        log.info("Reseña con ID {} actualizada correctamente", id);
        return actualizada;
    }

    /** Elimina una reseña por ID; lanza {@link RecursoNoEncontradoException} (404) si no existe previamente. */
    public void eliminar(Long id) {
        log.info("Eliminando reseña con ID: {}", id);
        if (!repo.existsById(id)) {
            log.warn("No se pudo eliminar: la reseña con ID {} no existe", id);
            throw new RecursoNoEncontradoException("La reseña con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Reseña con ID {} eliminada correctamente", id);
    }

    /**
     * Llama a {@link ComponenteClient#buscarPorId} para confirmar que el componente existe en ms-componentes.
     * Un 404 remoto se traduce a {@link RecursoNoEncontradoException}; cualquier otro fallo de Feign
     * (timeout, 5xx, conexión) se traduce a {@link ErrorComunicacionException} (502).
     */
    private void validarComponenteExiste(Long idComponente) {
        try {
            componenteClient.buscarPorId(idComponente);
        } catch (FeignException.NotFound e) {
            log.warn("El componente {} no existe en ms-componentes", idComponente);
            throw new RecursoNoEncontradoException("El componente " + idComponente + " no existe.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-componentes al validar el componente {}: {}", idComponente, e.getMessage());
            throw new ErrorComunicacionException("ms-componentes no respondió correctamente: " + e.getMessage());
        }
    }

    /** Copia los campos del DTO de request a la entidad (usado tanto en creación como en actualización). */
    private void mapearDatos(Resena r, ResenaRequestDTO dto) {
        r.setAutor(dto.autor());
        r.setComentario(dto.comentario());
        r.setCalificacion(dto.calificacion());
        r.setIdComponente(dto.idComponente());
    }

    /** Busca la entidad {@link Resena} por ID o lanza {@link RecursoNoEncontradoException} (404). */
    private Resena buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reseña con ID {} no encontrada", id);
                    return new RecursoNoEncontradoException("La reseña con ID " + id + " no existe.");
                });
    }

    /** Mapea la entidad {@link Resena} a {@link ResenaResponseDTO} para no exponer la entidad por HTTP. */
    private ResenaResponseDTO aResponseDTO(Resena r) {
        return new ResenaResponseDTO(r.getId(), r.getAutor(), r.getComentario(), r.getCalificacion(), r.getIdComponente());
    }
}
