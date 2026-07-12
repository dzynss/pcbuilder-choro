package com.pcbuilder.ms_notificaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una notificación (email o SMS) enviada a un usuario.
 * Mapeada a la tabla "notificaciones", cuyo esquema real vive en Liquibase
 * (db.changelog-master.xml); ddl-auto es "validate", así que esta clase debe coincidir con esa tabla.
 */
@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    /** Identificador autogenerado de la notificación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario destinatario; se valida contra ms-usuarios vía UsuarioClient antes de guardar. */
    @NotNull(message = "Tenís que decirme a qué loco le mando el mensaje")
    private Long idUsuario;

    /** Tipo de mensaje: EMAIL o SMS. */
    @NotBlank(message = "Ponle si es EMAIL o SMS, no seai pajarón")
    private String tipoMensaje;

    /** Texto del mensaje a enviar. */
    @NotBlank(message = "El mensaje no puede ir pelao")
    private String contenido;

    private String estado; // PENDIENTE o ENVIADO
    private LocalDateTime fechaEnvio;
}