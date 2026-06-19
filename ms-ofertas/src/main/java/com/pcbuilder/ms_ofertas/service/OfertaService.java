package com.pcbuilder.ms_ofertas.service;

import com.pcbuilder.ms_ofertas.entity.Oferta;
import com.pcbuilder.ms_ofertas.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository repo;

    public List<Oferta> listarTodas() { return repo.findAll(); }

    public Oferta buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Cupón mula, no existe."));
    }

    public Oferta buscarPorCodigo(String codigo) {
        return repo.findByCodigo(codigo.toUpperCase()).orElseThrow(() -> new RuntimeException("Ese código no sirve hermano."));
    }

    public Oferta guardar(Oferta oferta) {
        oferta.setCodigo(oferta.getCodigo().toUpperCase());
        return repo.save(oferta);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}