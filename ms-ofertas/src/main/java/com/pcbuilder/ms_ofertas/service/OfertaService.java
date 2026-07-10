package com.pcbuilder.ms_ofertas.service;

import com.pcbuilder.ms_ofertas.dto.OfertaRequestDTO;
import com.pcbuilder.ms_ofertas.dto.OfertaResponseDTO;
import com.pcbuilder.ms_ofertas.entity.Oferta;
import com.pcbuilder.ms_ofertas.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_ofertas.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfertaService {

    private final OfertaRepository repo;

    public List<OfertaResponseDTO> listarTodas() {
        log.info("Listando todas las ofertas");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public OfertaResponseDTO buscarPorId(Long id) {
        log.info("Buscando la oferta con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public OfertaResponseDTO buscarPorCodigo(String codigo) {
        log.info("Buscando la oferta con código: {}", codigo);
        Oferta oferta = repo.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> {
                    log.error("El código de oferta {} no existe", codigo);
                    return new RecursoNoEncontradoException("Ese código de oferta no existe.");
                });
        return aResponseDTO(oferta);
    }

    public OfertaResponseDTO guardar(OfertaRequestDTO dto) {
        log.info("Guardando la oferta con código: {}", dto.codigo());
        Oferta oferta = new Oferta();
        oferta.setCodigo(dto.codigo().toUpperCase());
        oferta.setPorcentajeDescuento(dto.porcentajeDescuento());
        Oferta guardada = repo.save(oferta);
        log.info("Oferta guardada con ID: {}", guardada.getId());
        return aResponseDTO(guardada);
    }

    /**
     * Actualiza el porcentaje de descuento de una oferta existente.
     * El código no se puede modificar por esta vía (es único y define el cupón).
     */
    public OfertaResponseDTO actualizar(Long id, OfertaRequestDTO dto) {
        log.info("Actualizando la oferta con ID: {}", id);
        Oferta oferta = buscarEntidadPorId(id);
        oferta.setPorcentajeDescuento(dto.porcentajeDescuento());
        Oferta actualizada = repo.save(oferta);
        log.info("Oferta ID {} actualizada correctamente", id);
        return aResponseDTO(actualizada);
    }

    public void eliminar(Long id) {
        log.warn("Eliminando la oferta con ID: {}", id);
        if (!repo.existsById(id)) {
            log.error("No se pudo eliminar: la oferta con ID {} no existe", id);
            throw new RecursoNoEncontradoException("El cupón con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Oferta ID {} eliminada", id);
    }

    /** Regla de negocio: aplica el descuento de la oferta sobre un monto base. */
    public double aplicarDescuento(OfertaResponseDTO oferta, double montoBase) {
        log.info("Aplicando descuento de la oferta {} sobre el monto {}", oferta.codigo(), montoBase);
        if (montoBase <= 0) {
            log.error("El monto base {} no es válido para aplicar un descuento", montoBase);
            throw new IllegalArgumentException("El monto base debe ser mayor a cero.");
        }
        if (!oferta.activa()) {
            log.warn("La oferta {} ya no está activa, no se puede aplicar el descuento", oferta.codigo());
            throw new IllegalStateException("La oferta " + oferta.codigo() + " ya no está activa.");
        }
        return montoBase - (montoBase * oferta.porcentajeDescuento() / 100.0);
    }

    private Oferta buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.error("El cupón con ID {} no existe", id);
                    return new RecursoNoEncontradoException("El cupón con ID " + id + " no existe.");
                });
    }

    private OfertaResponseDTO aResponseDTO(Oferta o) {
        return new OfertaResponseDTO(o.getId(), o.getCodigo(), o.getPorcentajeDescuento(), o.isActiva());
    }
}
