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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    public List<CotizacionResponseDTO> buscarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public CotizacionResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public List<CotizacionResponseDTO> buscarPorUsuario(Long idUsuario) {
        return repo.findByIdUsuario(idUsuario).stream().map(this::aResponseDTO).toList();
    }

    public Double calcularTotalPorUsuario(Long idUsuario) {
        return repo.findByIdUsuario(idUsuario).stream()
                .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                .sum();
    }

    /** Valida usuario y componente en sus respectivos microservicios y calcula el total con el precio real. */
    public CotizacionResponseDTO guardar(CotizacionRequestDTO dto) {
        validarUsuarioExiste(dto.idUsuario());
        ComponenteResponseDTO componente = obtenerComponente(dto.idComponente());

        Cotizacion c = new Cotizacion();
        c.setIdUsuario(dto.idUsuario());
        c.setIdComponente(dto.idComponente());
        c.setCantidad(dto.cantidad());
        c.setTotal(calcularTotal(componente, dto.cantidad()));

        return aResponseDTO(repo.save(c));
    }

    public CotizacionResponseDTO actualizar(Long id, CotizacionRequestDTO dto) {
        Cotizacion existente = buscarEntidadPorId(id);
        validarUsuarioExiste(dto.idUsuario());
        ComponenteResponseDTO componente = obtenerComponente(dto.idComponente());

        existente.setIdUsuario(dto.idUsuario());
        existente.setIdComponente(dto.idComponente());
        existente.setCantidad(dto.cantidad());
        existente.setTotal(calcularTotal(componente, dto.cantidad()));

        return aResponseDTO(repo.save(existente));
    }

    private double calcularTotal(ComponenteResponseDTO componente, Integer cantidad) {
        if (componente.precio() == null) {
            throw new ErrorComunicacionException(
                    "ms-componentes devolvió un precio inválido para el componente " + componente.id());
        }
        return componente.precio() * cantidad;
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("La cotización con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    private void validarUsuarioExiste(Long idUsuario) {
        try {
            usuarioClient.buscarPorId(idUsuario);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El usuario " + idUsuario + " no existe.");
        } catch (FeignException e) {
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }
    }

    private ComponenteResponseDTO obtenerComponente(Long idComponente) {
        try {
            return componenteClient.buscarPorId(idComponente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El componente " + idComponente + " no existe.");
        } catch (FeignException e) {
            throw new ErrorComunicacionException("ms-componentes no respondió correctamente: " + e.getMessage());
        }
    }

    private Cotizacion buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cotización con ID " + id + " no existe."));
    }

    private CotizacionResponseDTO aResponseDTO(Cotizacion c) {
        return new CotizacionResponseDTO(c.getId(), c.getIdUsuario(), c.getIdComponente(), c.getCantidad(), c.getTotal());
    }
}
