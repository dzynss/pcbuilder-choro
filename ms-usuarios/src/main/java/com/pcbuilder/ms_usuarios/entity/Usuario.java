package com.pcbuilder.ms_usuarios.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa un usuario del sistema, mapeada a la tabla
 * {@code usuarios} definida en db.changelog-master.xml (Liquibase es la
 * fuente de verdad del esquema; ddl-auto es "validate", no genera tablas).
 * Nunca se expone directamente por HTTP: los controllers usan
 * {@link com.pcbuilder.ms_usuarios.dto.UsuarioResponseDTO}.
 */
@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(unique = true)
    private String correo;
    private String password;
    private String rol; // valores esperados: "ADMIN" o "USER"
}