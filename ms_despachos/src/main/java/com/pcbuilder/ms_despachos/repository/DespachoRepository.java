package com.pcbuilder.ms_despachos.repository;

import com.pcbuilder.ms_despachos.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findByIdUsuario(Long idUsuario);
}