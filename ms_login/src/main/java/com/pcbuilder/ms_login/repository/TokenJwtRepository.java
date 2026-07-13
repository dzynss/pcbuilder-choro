package com.pcbuilder.ms_login.repository;

import com.pcbuilder.ms_login.entity.TokenJwt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de {@link TokenJwt}. Usado por {@link com.pcbuilder.ms_login.service.AuthService}
 * para registrar cada JWT emitido y para listar los tokens registrados.
 */
public interface TokenJwtRepository extends JpaRepository<TokenJwt, Long> {
}
