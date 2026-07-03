package com.pcbuilder.presupuesto_service.service;

import com.pcbuilder.presupuesto_service.dto.PresupuestoRequestDTO;
import com.pcbuilder.presupuesto_service.dto.PresupuestoResponseDTO;
import com.pcbuilder.presupuesto_service.entity.Presupuesto;
import com.pcbuilder.presupuesto_service.repository.PresupuestoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresupuestoService {

    private final PresupuestoRepository repo;

    public List<PresupuestoResponseDTO> listarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public PresupuestoResponseDTO guardar(PresupuestoRequestDTO dto) {
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setTotalAprobado(dto.totalAprobado());
        presupuesto.setTotalGastado(dto.totalGastado());
        presupuesto.setFechaRegistro(dto.fechaRegistro());
        presupuesto.setEstado(dto.estado());
        return aResponseDTO(repo.save(presupuesto));
    }

    private PresupuestoResponseDTO aResponseDTO(Presupuesto p) {
        return new PresupuestoResponseDTO(p.getId(), p.getTotalAprobado(), p.getTotalGastado(),
                p.getFechaRegistro(), p.getEstado());
    }
}
