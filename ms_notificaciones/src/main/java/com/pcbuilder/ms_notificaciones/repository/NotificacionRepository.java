package com.pcbuilder.ms_notificaciones.repository;

import com.pcbuilder.ms_notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de {@link Notificacion}; provee el CRUD estándar sobre la tabla "notificaciones".
 * Usado únicamente por {@code NotificacionService}.
 */
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}