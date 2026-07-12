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

/**
 * Lógica de negocio de cotizaciones. Orquesta {@link CotizacionRepository} para persistencia
 * y los clientes Feign {@link UsuarioClient} / {@link ComponenteClient} para garantizar
 * integridad referencial con ms-usuarios y ms-componentes: valida que el usuario exista
 * y calcula el total SIEMPRE con el precio real del componente (nunca con el precio que
 * pudiera venir manipulado en el request).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CotizacionService {

    private final CotizacionRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    /** Lista todas las cotizaciones (usado por CotizacionController.listar). */
    public List<CotizacionResponseDTO> buscarTodos() {
        log.info("Buscando todas las cotizaciones");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca una cotización por ID; lanza RecursoNoEncontradoException (→ 404) si no existe. */
    public CotizacionResponseDTO buscarPorId(Long id) {
        log.info("Buscando cotización con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /** Lista las cotizaciones de un usuario específico (por su ID, referenciado en ms-usuarios). */
    public List<CotizacionResponseDTO> buscarPorUsuario(Long idUsuario) {
        log.info("Buscando cotizaciones del usuario ID: {}", idUsuario);
        return repo.findByIdUsuario(idUsuario).stream().map(this::aResponseDTO).toList();
    }

    /** Suma los totales (ya calculados con precio real) de todas las cotizaciones de un usuario. */
    public Double calcularTotalPorUsuario(Long idUsuario) {
        log.info("Calculando el total gastado por el usuario ID: {}", idUsuario);
        Double total = repo.findByIdUsuario(idUsuario).stream()
                .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                .sum();
        log.info("Total calculado para el usuario ID {}: {}", idUsuario, total);
        return total;
    }

    /**
     * Crea una cotización nueva. Valida el usuario en ms-usuarios ({@link UsuarioClient})
     * y obtiene el componente real (con su precio) desde ms-componentes ({@link ComponenteClient})
     * antes de persistir, para que el total se calcule con datos confiables y no con lo que
     * mande el cliente en el request.
     */
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

    /**
     * Actualiza una cotización existente: re-valida usuario y componente contra los
     * microservicios remotos y recalcula el total con el precio real vigente
     * (no se reutiliza el total anterior ni el del request).
     */
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

    /**
     * Calcula el total de la cotización multiplicando el PRECIO REAL del componente
     * (obtenido de ms-componentes vía {@link ComponenteClient}, nunca del request del cliente)
     * por la cantidad solicitada. Punto clave de integridad: evita que el usuario manipule precios.
     */
    private double calcularTotal(ComponenteResponseDTO componente, Integer cantidad) {
        if (componente.precio() == null) {
            log.error("ms-componentes devolvió un precio inválido para el componente {}", componente.id());
            throw new ErrorComunicacionException(
                    "ms-componentes devolvió un precio inválido para el componente " + componente.id());
        }
        return componente.precio() * cantidad;
    }

    /** Elimina una cotización por ID; lanza RecursoNoEncontradoException (→ 404) si no existe. */
    public void eliminar(Long id) {
        log.info("Eliminando cotización con ID: {}", id);
        if (!repo.existsById(id)) {
            log.warn("No se pudo eliminar: la cotización con ID {} no existe", id);
            throw new RecursoNoEncontradoException("La cotización con ID " + id + " no existe.");
        }
        repo.deleteById(id);
        log.info("Cotización con ID {} eliminada correctamente", id);
    }

    /**
     * Verifica que el usuario exista llamando a ms-usuarios vía {@link UsuarioClient}.
     * FeignException.NotFound → RecursoNoEncontradoException (404); cualquier otro
     * FeignException (timeout, 5xx, etc.) → ErrorComunicacionException (502).
     */
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

    /**
     * Obtiene el componente (incluyendo su precio real) desde ms-componentes vía
     * {@link ComponenteClient}. FeignException.NotFound → RecursoNoEncontradoException (404);
     * cualquier otro FeignException → ErrorComunicacionException (502).
     */
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

    /** Obtiene la entidad Cotizacion desde el repositorio o lanza RecursoNoEncontradoException (→ 404). */
    private Cotizacion buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cotización con ID {} no encontrada", id);
                    return new RecursoNoEncontradoException("La cotización con ID " + id + " no existe.");
                });
    }

    /** Convierte la entidad Cotizacion al DTO expuesto por el controller. */
    private CotizacionResponseDTO aResponseDTO(Cotizacion c) {
        return new CotizacionResponseDTO(c.getId(), c.getIdUsuario(), c.getIdComponente(), c.getCantidad(), c.getTotal());
    }
}
