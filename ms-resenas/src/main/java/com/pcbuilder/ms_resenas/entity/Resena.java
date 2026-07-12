package com.pcbuilder.ms_resenas.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa una reseña de un componente.
 * Se mapea a la tabla "resenas", cuyo esquema real vive en el changelog de Liquibase
 * (db.changelog-master.xml); ddl-auto es "validate", por lo que esta clase debe coincidir con esa tabla.
 * Nunca se expone directamente por HTTP: el controller/service la traducen a ResenaResponseDTO.
 */
@Entity
@Table(name = "resenas")
@Data
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String autor;
    private String comentario;
    private Integer calificacion; // 1 a 5
    private Long idComponente; // ID de la pieza en ms-componentes (validado vía Feign, no es una FK real)
}