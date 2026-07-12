package com.pcbuilder.ms_componentes.repository;

import com.pcbuilder.ms_componentes.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA de {@link Categoria}. Usado por
 * {@link com.pcbuilder.ms_componentes.service.CategoriaService} (CRUD de categorías) y por
 * {@link com.pcbuilder.ms_componentes.service.ComponenteService} (para resolver la categoría
 * de un componente al crearlo/actualizarlo).
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    /** Busca una categoría por nombre exacto; usado por DataLoader para ubicar la categoría "CPU" al sembrar datos. */
    Optional<Categoria> findByNombre(String nombre);
}
