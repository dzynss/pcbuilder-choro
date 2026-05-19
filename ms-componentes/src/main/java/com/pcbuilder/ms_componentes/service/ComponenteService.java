package com.pcbuilder.ms_componentes.service;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponenteService {
    private final ComponenteRepository repo;

    public List<Componente> buscarTodos() { return repo.findAll(); }
    public Componente buscarPorId(Long id) { return repo.findById(id).orElseThrow(); }
    public Componente guardar(Componente c) { return repo.save(c); }
    public void eliminar(Long id) { repo.deleteById(id); }
}