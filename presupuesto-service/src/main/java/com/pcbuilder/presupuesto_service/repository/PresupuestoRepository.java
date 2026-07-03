package com.pcbuilder.presupuesto_service.repository;

import com.pcbuilder.presupuesto_service.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
}
