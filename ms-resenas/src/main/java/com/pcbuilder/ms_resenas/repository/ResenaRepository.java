package com.pcbuilder.ms_resenas.repository;

import com.pcbuilder.ms_resenas.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    // sistema de estrellas (idk)
    List<Resena> findByCalificacion(Integer calificacion);
}