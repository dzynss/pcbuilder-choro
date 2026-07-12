package com.pcbuilder.ms_despachos.repository;

import com.pcbuilder.ms_despachos.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA de despachos, usado por DespachoService para el CRUD contra la tabla "despachos".
 */
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    /** Busca todos los despachos asociados a un usuario (derived query por idUsuario). */
    List<Despacho> findByIdUsuario(Long idUsuario);
}