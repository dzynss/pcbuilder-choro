package com.pcbuilder.ms_ofertas.repository;

import com.pcbuilder.ms_ofertas.entity.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    Optional<Oferta> findByCodigo(String codigo);
}