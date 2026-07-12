package com.pcbuilder.ms_cotizaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;

/**
 * Repositorio JPA de {@link Cotizacion}; provee el CRUD estándar de Spring Data
 * y es usado exclusivamente por {@code CotizacionService}.
 */
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    /** Busca todas las cotizaciones asociadas a un usuario (id validado externamente en ms-usuarios). */
    List<Cotizacion> findByIdUsuario(Long idUsuario);
}