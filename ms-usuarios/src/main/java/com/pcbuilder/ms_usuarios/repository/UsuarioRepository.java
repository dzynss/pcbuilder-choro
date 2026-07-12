package com.pcbuilder.ms_usuarios.repository;

import com.pcbuilder.ms_usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA de {@link Usuario}, usado por {@link com.pcbuilder.ms_usuarios.service.UsuarioService}
 * para todo el acceso a datos (CRUD estándar heredado de JpaRepository).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /** Busca un usuario cuyo correo y password coincidan; se usa para validar el login. */
    Optional<Usuario> findByCorreoAndPassword(String correo, String password);
}