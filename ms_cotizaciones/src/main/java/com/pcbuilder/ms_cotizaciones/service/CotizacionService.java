package com.pcbuilder.ms_cotizaciones.service;

import com.pcbuilder.ms_cotizaciones.client.ComponenteClient;
import com.pcbuilder.ms_cotizaciones.client.UsuarioClient;
import com.pcbuilder.ms_cotizaciones.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.pcbuilder.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.pcbuilder.ms_cotizaciones.entity.Cotizacion;
import com.pcbuilder.ms_cotizaciones.exception.ErrorComunicacionException;
import com.pcbuilder.ms_cotizaciones.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_cotizaciones.repository.CotizacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotizacionService {

    private final CotizacionRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    public List<CotizacionResponseDTO> buscarTodos() {
        log.info("Buscando todas las cotizaciones");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public CotizacionResponseDTO buscarPorId(Long id) {
        log.info("Buscando cotización con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public List<CotizacionResponseDTO> buscarPorUsuario(Long idUsuario) {
        log.info("Buscando cotizaciones del usuario ID: {}", idUsuario);
        return repo.findByIdUsuario(idUsuario).stream().map(this::aResponseDTO).toList();
    }

    public Double calcularTotalPorUsuario(Long idUsuario) {
        log.info("Calculando el total gastado por el usuario ID: {}", idUsuario);
        Double total = repo.findByIdUsuario(idUsuario).stream()
                .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                .sum();
        log.info("Total calculado para el usuario ID {}: {}", idUsuario, total);
        return total;
    }

    /** Valida usuario y componente en sus respectivos microservicios y calcula el total con el precio real. */
    public CotizacionResponseDTO guardar(CotizacionRequestDTO dto) {
        log.info("Creando cotización para el usuario {} con el componente {}", dto.idUsuario(), dto.idComponente());
        validarUsuarioExiste(dto.idUsuario());
        ComponenteResponseDTO componente = obtenerComponente(dto.idComponente());

        Cotizacion c = new Cotizacion();
        c.setIdUsuario(dto.idUsuario());
        c.setIdComponente(dto.idComponente());
        c.setCantidad(dto.cantidad());
        c.setTotal(calcularTotal(componente, dto.cantidad()));

        CotizacionResponseDTO creada = aResponseDTO(repo.save(c));
        log.info("Cotización creada con ID: {}", creada.id());
        return creada;
    }

    public CotizacionResponseDTO actualizar(Long id, CotizacionRequestDTO dto) {
        log.info("Actualizando cotización con ID: {}", id);
        Cotizacion existente = buscarEntidadPorId(id);
        validarUsuarioExiste(dto.idUsuario());
        ComponenteResponseDTO componente = obtenerComponente(dto.idComponente());

        existente.setIdUsuario(dto.idUsuario());
        existente.setIdComponente(dto.idComponente());
        existente.setCantidad(dto.cantidad());
        existente.setTotal(calcularTotal(componente, dto.cantidad()));

        CotizacionResponseDTO actualizada = aResponseDTO(repo.save(existente));
        log.info("Cotización con ID {} actualizada correctamente", id);
        return actualizada;
    }

    private double calcularTotal(ComponenteResponseDTO componente, Integer cantidad) {
        if (componente.precio() == null) {
            log.error("ms-componentes devolvió un precio inválido para el componente {}", componente.id());
            throw new ErrorComunicacionException(
                    "ms-componentes devolvió un precio inválido para el componente " + componente.id());
        }
        return componente.precio() * cantidad;
    }

    public void eliminar(Long id) {
        log.info("Eliminando cotización con ID: {}", id);
        if (!repo.existsById(id)) {
            log.warn("No se pudo eliminar: la cotización con ID {} no existe", id);
            throw new RecursoNoEncontradoException("La cotización con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Cotización con ID {} eliminada correctamente", id);
    }

    private void validarUsuarioExiste(Long idUsuario) {
        try {
            usuarioClient.buscarPorId(idUsuario);
        } catch (FeignException.NotFound e) {
            log.warn("El usuario {} no existe en ms-usuarios", idUsuario);
            throw new RecursoNoEncontradoException("El usuario " + idUsuario + " no existe.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-usuarios al validar el usuario {}: {}", idUsuario, e.getMessage());
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }
    }

    private ComponenteResponseDTO obtenerComponente(Long idComponente) {
        try {
            return componenteClient.buscarPorId(idComponente);
        } catch (FeignException.NotFound e) {
            log.warn("El componente {} no existe en ms-componentes", idComponente);
            throw new RecursoNoEncontradoException("El componente " + idComponente + " no existe.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-componentes al validar el componente {}: {}", idComponente, e.getMessage());
            throw new ErrorComunicacionException("ms-componentes no respondió correctamente: " + e.getMessage());
        }
    }

    private Cotizacion buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cotización con ID {} no encontrada", id);
                    return new RecursoNoEncontradoException("La cotización con ID " + id + " no existe.");
                });
    }

    private CotizacionResponseDTO aResponseDTO(Cotizacion c) {
        return new CotizacionResponseDTO(c.getId(), c.getIdUsuario(), c.getIdComponente(), c.getCantidad(), c.getTotal());
    }
}
