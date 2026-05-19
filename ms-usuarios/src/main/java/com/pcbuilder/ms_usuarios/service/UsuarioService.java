package com.pcbuilder.ms_usuarios.service;

import com.pcbuilder.ms_usuarios.entity.Usuario;
import com.pcbuilder.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository repo;

    // metodos del CRUD pa salvar la nota
    public List<Usuario> buscarTodos() { return repo.findAll(); }
    public Usuario buscarPorId(Long id) { return repo.findById(id).orElseThrow(); }
    public Usuario guardar(Usuario u) { return repo.save(u); }
    public void eliminar(Long id) { repo.deleteById(id); }

    // EL SANTO LOGIN !!!!
    public Usuario login(String correo, String password) {
        return repo.findByCorreoAndPassword(correo, password)
                .orElseThrow(() -> new RuntimeException("Error! Correo o clave charcha (perkinaso)."));
    }
}