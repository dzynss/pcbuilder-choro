package com.pcbuilder.ms_resenas.repository;

import com.pcbuilder.ms_resenas.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA de {@link Resena}, usado por {@code ResenaService} para leer/escribir en la tabla "resenas".
 */
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    /** Filtra reseñas por cantidad de estrellas (calificación de 1 a 5). */
    List<Resena> findByCalificacion(Integer calificacion);
}