package com.pcbuilder.ms_despachos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "despachos")
@Data
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Falta el ID del cliente al que le mandamos la weá")
    private Long idUsuario;

    @NotBlank(message = "Vo' soy vio, ponle la dirección o la weá no llega")
    private String direccionEnvio;

    private String empresaTransporte; 

    private String estadoSeguimiento; 

    private LocalDateTime fechaDespacho;
}