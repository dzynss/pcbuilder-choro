package com.pcbuilder.ms_notificaciones.repository;

import com.pcbuilder.ms_notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}