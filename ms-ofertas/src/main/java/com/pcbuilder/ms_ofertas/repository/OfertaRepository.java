package com.pcbuilder.ms_ofertas.repository;

import com.pcbuilder.ms_ofertas.entity.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA de Oferta. Provee el CRUD estándar de JpaRepository
 * y es usado por OfertaService para toda la persistencia.
 */
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    /** Busca una oferta por su código de cupón (único), usado para validar cupones ingresados por el usuario. */
    Optional<Oferta> findByCodigo(String codigo);
}