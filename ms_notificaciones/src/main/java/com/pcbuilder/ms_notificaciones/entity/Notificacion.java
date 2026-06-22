package com.pcbuilder.ms_notificaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tenís que decirme a qué loco le mando el mensaje")
    private Long idUsuario;

    @NotBlank(message = "Ponle si es EMAIL o SMS, no seai pajarón")
    private String tipoMensaje;

    @NotBlank(message = "El mensaje no puede ir pelao")
    private String contenido;

    private String estado; // PENDIENTE o ENVIADO
    private LocalDateTime fechaEnvio;
}