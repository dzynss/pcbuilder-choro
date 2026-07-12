package com.pcbuilder.ms_login.repository;

import com.pcbuilder.ms_login.entity.HistorialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de {@link HistorialLogin}. Usado por {@link com.pcbuilder.ms_login.service.AuthService}
 * para guardar cada intento de login y para listar el historial completo.
 */
public interface HistorialRepository extends JpaRepository<HistorialLogin, Long> {
}