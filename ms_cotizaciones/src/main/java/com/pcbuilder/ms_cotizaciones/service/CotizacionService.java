package com.pcbuilder.ms_cotizaciones.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;
import com.pcbuilder.ms_cotizaciones.repository.CotizacionRepository;

@Service
public class CotizacionService {
    
    private final CotizacionRepository repo;
    private final RestTemplate restTemplate;

    public CotizacionService(CotizacionRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public List<Cotizacion> buscarTodos() { return repo.findAll(); }
    public Cotizacion buscarPorId(Long id) { return repo.findById(id).orElseThrow(); }
    public void eliminar(Long id) { repo.deleteById(id); }
    public List<Cotizacion> buscarPorUsuario(Long idUsuario) { return repo.findByIdUsuario(idUsuario); }

    // Clase piola pa' recibir el precio del otro microservicio
    static class ComponenteDTO {
        public Double precio;
    }

    // EL GUARDADO CON LLAMADA A 2 MICROSERVICIOS DISTINTOS
    public Cotizacion guardar(Cotizacion c) {
        try {
            // 1. Validamos que el loco exista en ms-usuarios (Puerto 8082)
            restTemplate.getForObject("http://localhost:8082/api/usuarios/" + c.getIdUsuario(), Object.class);
            
            // 2. Traemos la pieza del ms-componentes (Puerto 8081) pa' sacar el precio
            ComponenteDTO comp = restTemplate.getForObject("http://localhost:8081/api/componentes/" + c.getIdComponente(), ComponenteDTO.class);
            
            // 3. Calculamos el total de las lucas
            c.setTotal(comp.precio * c.getCantidad());
            
            return repo.save(c);
        } catch (Exception e) {
            throw new RuntimeException("¡Te pillé po compadre! El usuario no existe o la pieza no está registrada.");
        }
    }

    // Calcular el total gastado por un loco
    public Double calcularTotalPorUsuario(Long idUsuario) {
        List<Cotizacion> lista = repo.findByIdUsuario(idUsuario);
        return lista.stream().mapToDouble(Cotizacion::getTotal).sum();
    }
}