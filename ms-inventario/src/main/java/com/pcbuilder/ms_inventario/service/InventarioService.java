package com.pcbuilder.ms_inventario.service;

import com.pcbuilder.ms_inventario.dto.InventarioRequestDTO;
import com.pcbuilder.ms_inventario.dto.InventarioResponseDTO;
import com.pcbuilder.ms_inventario.entity.Inventario;
import com.pcbuilder.ms_inventario.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository repo;

    public List<InventarioResponseDTO> listarTodos() {
        log.info("Listando todos los registros de inventario");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public InventarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando el registro de inventario con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public InventarioResponseDTO guardar(InventarioRequestDTO dto) {
        log.info("Guardando nuevo registro de inventario pal componente ID: {}", dto.idComponente());
        Inventario inventario = new Inventario();
        inventario.setIdComponente(dto.idComponente());
        inventario.setCantidadDisponible(dto.cantidadDisponible());
        inventario.setUbicacionBodega(dto.ubicacionBodega());
        Inventario guardado = repo.save(inventario);
        log.info("Registro de inventario guardado con ID: {}", guardado.getId());
        return aResponseDTO(guardado);
    }

    public InventarioResponseDTO actualizar(Long id, InventarioRequestDTO dto) {
        log.info("Actualizando el registro de inventario con ID: {}", id);
        Inventario inventario = buscarEntidadPorId(id);
        inventario.setIdComponente(dto.idComponente());
        inventario.setCantidadDisponible(dto.cantidadDisponible());
        inventario.setUbicacionBodega(dto.ubicacionBodega());
        Inventario actualizado = repo.save(inventario);
        log.info("Registro de inventario ID {} actualizado correctamente", id);
        return aResponseDTO(actualizado);
    }

    public void eliminar(Long id) {
        log.warn("Eliminando el registro de inventario con ID: {}", id);
        if (!repo.existsById(id)) {
            log.error("No se pudo eliminar: el registro de inventario con ID {} no existe", id);
            throw new RecursoNoEncontradoException("El registro de inventario con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Registro de inventario ID {} eliminado", id);
    }

    private Inventario buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.error("El registro de inventario con ID {} no existe", id);
                    return new RecursoNoEncontradoException("El registro de inventario con ID " + id + " no existe.");
                });
    }

    private InventarioResponseDTO aResponseDTO(Inventario i) {
        return new InventarioResponseDTO(i.getId(), i.getIdComponente(), i.getCantidadDisponible(),
                i.getUbicacionBodega(), i.getUltimaActualizacion());
    }
}
