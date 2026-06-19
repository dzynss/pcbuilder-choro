package com.pcbuilder.ms_soporte.repository;

import com.pcbuilder.ms_soporte.entity.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketSoporte, Long> {
    List<TicketSoporte> findByIdUsuario(Long idUsuario);
}