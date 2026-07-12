package com.pcbuilder.ms_componentes.repository;
import com.pcbuilder.ms_componentes.entity.Componente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de {@link Componente}. Usado por
 * {@link com.pcbuilder.ms_componentes.service.ComponenteService} para el CRUD del catálogo
 * y por {@link com.pcbuilder.ms_componentes.service.CategoriaService} para validar la FK
 * antes de borrar una categoría.
 */
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
    /** Query derivada: indica si existen componentes asociados a la categoría dada (evita borrar una categoría en uso). */
    boolean existsByCategoriaId(Long categoriaId);
}