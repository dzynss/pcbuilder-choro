package com.pcbuilder.ms_soporte.repository;

import com.pcbuilder.ms_soporte.entity.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA de tickets de soporte; usado por {@code SoporteService} para
 * todo el acceso a datos (CRUD estándar heredado de JpaRepository).
 */
public interface TicketRepository extends JpaRepository<TicketSoporte, Long> {

    /** Busca todos los tickets abiertos/cerrados asociados a un usuario específico. */
    List<TicketSoporte> findByIdUsuario(Long idUsuario);
}