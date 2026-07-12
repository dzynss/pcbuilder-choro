package com.pcbuilder.ms_despachos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un despacho/envío. Mapea la tabla "despachos",
 * cuyo esquema real es administrado por Liquibase (db.changelog-master.xml, ddl-auto: validate).
 * Lombok @Data genera getters/setters/equals/hashCode usados por el service y el mapeo a DTO.
 */
@Entity
@Table(name = "despachos")
@Data
public class Despacho {

    /** Identificador autogenerado (IDENTITY) de la fila en la tabla despachos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario destinatario; se valida contra ms-usuarios vía Feign antes de persistir. */
    @NotNull(message = "Falta el ID del cliente al que le mandamos la weá")
    private Long idUsuario;

    @NotBlank(message = "Vo' soy vio, ponle la dirección o la weá no llega")
    private String direccionEnvio;

    private String empresaTransporte;

    /** Estado de seguimiento del paquete; solo admite los valores definidos en DespachoService.ESTADOS_VALIDOS. */
    private String estadoSeguimiento;

    private LocalDateTime fechaDespacho;
}