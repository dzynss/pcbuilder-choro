package com.pcbuilder.ms_inventario.service;

import com.pcbuilder.ms_inventario.entity.Inventario;
import com.pcbuilder.ms_inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository repo;

    public List<Inventario> listarTodos() { return repo.findAll(); }
    
    public Inventario buscarPorId(Long id) { 
        return repo.findById(id).orElseThrow(() -> new RuntimeException("¡Pifia! Registro no encontrado")); 
    }
    
    public Inventario guardar(Inventario inventario) { return repo.save(inventario); }
    
    public void eliminar(Long id) { repo.deleteById(id); }
}