package com.pcbuilder.ms_login.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_logins")
@Data
public class HistorialLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String correoUsuario;
    private LocalDateTime fechaHora;
    private String estado; // ej: "EXITOSO" o "FALLIDO"
}