package com.pcbuilder.ms_inventario.repository;

import com.pcbuilder.ms_inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByIdComponente(Long idComponente);
}