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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository repo;
    private final ComponenteClient componenteClient;

    public List<ResenaResponseDTO> buscarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public ResenaResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public List<ResenaResponseDTO> buscarPorEstrellas(Integer calificacion) {
        return repo.findByCalificacion(calificacion).stream().map(this::aResponseDTO).toList();
    }

    public ResenaResponseDTO guardar(ResenaRequestDTO dto) {
        validarComponenteExiste(dto.idComponente());
        Resena r = new Resena();
        mapearDatos(r, dto);
        return aResponseDTO(repo.save(r));
    }

    public ResenaResponseDTO actualizar(Long id, ResenaRequestDTO dto) {
        validarComponenteExiste(dto.idComponente());
        Resena existente = buscarEntidadPorId(id);
        mapearDatos(existente, dto);
        return aResponseDTO(repo.save(existente));
    }

    public void eliminar(Long id) {
        buscarEntidadPorId(id);
        repo.deleteById(id);
    }

    private void validarComponenteExiste(Long idComponente) {
        try {
            componenteClient.buscarPorId(idComponente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El componente " + idComponente + " no existe.");
        } catch (FeignException e) {
            throw new ErrorComunicacionException("ms-componentes no respondió correctamente: " + e.getMessage());
        }
    }

    private void mapearDatos(Resena r, ResenaRequestDTO dto) {
        r.setAutor(dto.autor());
        r.setComentario(dto.comentario());
        r.setCalificacion(dto.calificacion());
        r.setIdComponente(dto.idComponente());
    }

    private Resena buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reseña con ID " + id + " no existe."));
    }

    private ResenaResponseDTO aResponseDTO(Resena r) {
        return new ResenaResponseDTO(r.getId(), r.getAutor(), r.getComentario(), r.getCalificacion(), r.getIdComponente());
    }
}
