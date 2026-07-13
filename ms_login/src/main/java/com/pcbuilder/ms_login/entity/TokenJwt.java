package com.pcbuilder.ms_login.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que registra cada JWT emitido tras un login exitoso.
 * Complementa a {@link HistorialLogin}: mientras esa tabla registra el intento de login,
 * esta persiste el token efectivamente entregado al usuario, junto con su vigencia.
 * Mapeada a la tabla "tokens_jwt" creada por Liquibase en db.changelog-master.xml
 * (ddl-auto=validate, por lo que el esquema debe coincidir exactamente con ese changelog).
 */
@Entity
@Table(name = "tokens_jwt")
@Data
public class TokenJwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correoUsuario;

    @Column(length = 500)
    private String token;

    private LocalDateTime fechaEmision;
    private LocalDateTime fechaExpiracion;
}
