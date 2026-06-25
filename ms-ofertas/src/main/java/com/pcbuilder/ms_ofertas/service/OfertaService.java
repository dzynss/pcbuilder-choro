package com.pcbuilder.ms_ofertas.service;

import com.pcbuilder.ms_ofertas.dto.OfertaRequestDTO;
import com.pcbuilder.ms_ofertas.dto.OfertaResponseDTO;
import com.pcbuilder.ms_ofertas.entity.Oferta;
import com.pcbuilder.ms_ofertas.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_ofertas.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository repo;

    public List<OfertaResponseDTO> listarTodas() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public OfertaResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public OfertaResponseDTO buscarPorCodigo(String codigo) {
        Oferta oferta = repo.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ese código de oferta no existe."));
        return aResponseDTO(oferta);
    }

    public OfertaResponseDTO guardar(OfertaRequestDTO dto) {
        Oferta oferta = new Oferta();
        oferta.setCodigo(dto.codigo().toUpperCase());
        oferta.setPorcentajeDescuento(dto.porcentajeDescuento());
        return aResponseDTO(repo.save(oferta));
    }

    public void eliminar(Long id) {
        buscarEntidadPorId(id);
        repo.deleteById(id);
    }

    /** Regla de negocio: aplica el descuento de la oferta sobre un monto base. */
    public double aplicarDescuento(OfertaResponseDTO oferta, double montoBase) {
        if (!oferta.activa()) {
            throw new IllegalStateException("La oferta " + oferta.codigo() + " ya no está activa.");
        }
        return montoBase - (montoBase * oferta.porcentajeDescuento() / 100.0);
    }

    private Oferta buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cupón con ID " + id + " no existe."));
    }

    private OfertaResponseDTO aResponseDTO(Oferta o) {
        return new OfertaResponseDTO(o.getId(), o.getCodigo(), o.getPorcentajeDescuento(), o.isActiva());
    }
}
