package com.pcbuilder.ms_usuarios.service;

import com.pcbuilder.ms_usuarios.dto.LoginRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioResponseDTO;
import com.pcbuilder.ms_usuarios.entity.Usuario;
import com.pcbuilder.ms_usuarios.exception.CredencialesInvalidasException;
import com.pcbuilder.ms_usuarios.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_usuarios.exception.SolicitudInvalidaException;
import com.pcbuilder.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;

    public List<UsuarioResponseDTO> buscarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        if (dto.password() == null || dto.password().isBlank()) {
            throw new SolicitudInvalidaException("La contraseña es obligatoria al crear un usuario.");
        }
        Usuario u = new Usuario();
        u.setNombre(dto.nombre());
        u.setCorreo(dto.correo());
        u.setPassword(dto.password());
        u.setRol(dto.rol());
        return aResponseDTO(repo.save(u));
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario existente = buscarEntidadPorId(id);
        existente.setNombre(dto.nombre());
        existente.setCorreo(dto.correo());
        if (dto.password() != null && !dto.password().isBlank()) {
            existente.setPassword(dto.password());
        }
        existente.setRol(dto.rol());
        return aResponseDTO(repo.save(existente));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El usuario con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    public UsuarioResponseDTO login(LoginRequestDTO credenciales) {
        Usuario usuario = repo.findByCorreoAndPassword(credenciales.correo(), credenciales.password())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o clave incorrectos."));
        return aResponseDTO(usuario);
    }

    private Usuario buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con ID " + id + " no existe."));
    }

    private UsuarioResponseDTO aResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
