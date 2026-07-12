package com.pcbuilder.ms_inventario.repository;

import com.pcbuilder.ms_inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA de Inventario. Provee el CRUD estándar de JpaRepository
 * y es usado por InventarioService para toda la persistencia.
 */
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    /** Busca el registro de inventario asociado a un componente específico (por su ID en ms-componentes). */
    Optional<Inventario> findByIdComponente(Long idComponente);
}