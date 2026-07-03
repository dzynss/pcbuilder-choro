package com.pcbuilder.ms_componentes.service;

import com.pcbuilder.ms_componentes.dto.ComponenteRequestDTO;
import com.pcbuilder.ms_componentes.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_componentes.entity.Categoria;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_componentes.repository.CategoriaRepository;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponenteService {

    private final ComponenteRepository repo;
    private final CategoriaRepository categoriaRepo;

    public List<ComponenteResponseDTO> buscarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public ComponenteResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public ComponenteResponseDTO guardar(ComponenteRequestDTO dto) {
        Componente c = new Componente();
        mapearDatos(c, dto);
        return aResponseDTO(repo.save(c));
    }

    public ComponenteResponseDTO actualizar(Long id, ComponenteRequestDTO dto) {
        Componente existente = buscarEntidadPorId(id);
        mapearDatos(existente, dto);
        return aResponseDTO(repo.save(existente));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El componente con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    private void mapearDatos(Componente c, ComponenteRequestDTO dto) {
        Categoria categoria = categoriaRepo.findById(dto.idCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría con ID " + dto.idCategoria() + " no existe."));
        c.setNombre(dto.nombre());
        c.setMarca(dto.marca());
        c.setPrecio(dto.precio());
        c.setStock(dto.stock());
        c.setCategoria(categoria);
    }

    private Componente buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El componente con ID " + id + " no existe."));
    }

    private ComponenteResponseDTO aResponseDTO(Componente c) {
        String nombreCategoria = c.getCategoria() != null ? c.getCategoria().getNombre() : null;
        return new ComponenteResponseDTO(c.getId(), c.getNombre(), c.getMarca(), c.getPrecio(), c.getStock(), nombreCategoria);
    }
}
