package com.pcbuilder.ms_usuarios.repository;

import com.pcbuilder.ms_usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // aca se busca si existe un loco con ese correo y clave
    Optional<Usuario> findByCorreoAndPassword(String correo, String password);
}