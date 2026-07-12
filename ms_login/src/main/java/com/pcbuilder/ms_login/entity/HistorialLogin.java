package com.pcbuilder.ms_login.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que registra cada intento de login (exitoso o fallido).
 * Es la ÚNICA tabla propia de ms_login: este servicio no tiene tabla de usuarios,
 * las credenciales reales se validan contra ms-usuarios vía {@link com.pcbuilder.ms_login.client.UsuarioClient}.
 * Mapeada a la tabla "historial_logins" creada por Liquibase en db.changelog-master.xml
 * (ddl-auto=validate, por lo que el esquema debe coincidir exactamente con este changelog).
 */
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