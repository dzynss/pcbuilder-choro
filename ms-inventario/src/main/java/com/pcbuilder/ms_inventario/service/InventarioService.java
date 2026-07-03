package com.pcbuilder.ms_inventario.service;

import com.pcbuilder.ms_inventario.dto.InventarioRequestDTO;
import com.pcbuilder.ms_inventario.dto.InventarioResponseDTO;
import com.pcbuilder.ms_inventario.entity.Inventario;
import com.pcbuilder.ms_inventario.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository repo;

    public List<InventarioResponseDTO> listarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public InventarioResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public InventarioResponseDTO guardar(InventarioRequestDTO dto) {
        Inventario inventario = new Inventario();
        inventario.setIdComponente(dto.idComponente());
        inventario.setCantidadDisponible(dto.cantidadDisponible());
        inventario.setUbicacionBodega(dto.ubicacionBodega());
        return aResponseDTO(repo.save(inventario));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El registro de inventario con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    private Inventario buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El registro de inventario con ID " + id + " no existe."));
    }

    private InventarioResponseDTO aResponseDTO(Inventario i) {
        return new InventarioResponseDTO(i.getId(), i.getIdComponente(), i.getCantidadDisponible(),
                i.getUbicacionBodega(), i.getUltimaActualizacion());
    }
}
