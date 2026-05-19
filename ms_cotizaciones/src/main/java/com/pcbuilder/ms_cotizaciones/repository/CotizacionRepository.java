package com.pcbuilder.ms_cotizaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    // busqueda especial: Traer todas las cotizaciones de un solo loco
    List<Cotizacion> findByIdUsuario(Long idUsuario);
}