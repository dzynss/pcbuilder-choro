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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repo;

    public List<UsuarioResponseDTO> buscarTodos() {
        log.info("Buscando todos los usuarios");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        log.info("Creando usuario con correo: {}", dto.correo());
        if (dto.password() == null || dto.password().isBlank()) {
            log.warn("Intento de crear usuario con correo {} sin contraseña", dto.correo());
            throw new SolicitudInvalidaException("La contraseña es obligatoria al crear un usuario.");
        }
        Usuario u = new Usuario();
        u.setNombre(dto.nombre());
        u.setCorreo(dto.correo());
        u.setPassword(dto.password());
        u.setRol(dto.rol());
        UsuarioResponseDTO creado = aResponseDTO(repo.save(u));
        log.info("Usuario creado con ID: {}", creado.id());
        return creado;
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);
        Usuario existente = buscarEntidadPorId(id);
        existente.setNombre(dto.nombre());
        existente.setCorreo(dto.correo());
        if (dto.password() != null && !dto.password().isBlank()) {
            existente.setPassword(dto.password());
        }
        existente.setRol(dto.rol());
        UsuarioResponseDTO actualizado = aResponseDTO(repo.save(existente));
        log.info("Usuario con ID {} actualizado correctamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (!repo.existsById(id)) {
            log.warn("No se pudo eliminar: el usuario con ID {} no existe", id);
            throw new RecursoNoEncontradoException("El usuario con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Usuario con ID {} eliminado correctamente", id);
    }

    public UsuarioResponseDTO login(LoginRequestDTO credenciales) {
        log.info("Intento de login para el correo: {}", credenciales.correo());
        Usuario usuario = repo.findByCorreoAndPassword(credenciales.correo(), credenciales.password())
                .orElseThrow(() -> {
                    log.warn("Credenciales inválidas para el correo: {}", credenciales.correo());
                    return new CredencialesInvalidasException("Correo o clave incorrectos.");
                });
        log.info("Login exitoso para el correo: {}", credenciales.correo());
        return aResponseDTO(usuario);
    }

    private Usuario buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con ID {} no encontrado", id);
                    return new RecursoNoEncontradoException("El usuario con ID " + id + " no existe.");
                });
    }

    private UsuarioResponseDTO aResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
