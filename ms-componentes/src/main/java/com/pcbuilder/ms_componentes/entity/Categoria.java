package com.pcbuilder.ms_componentes.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa una categoría del catálogo (ej. "CPU", "RAM").
 * Se mapea a la tabla "categorias", cuyo esquema real vive en el changelog de
 * Liquibase (db.changelog-master.xml); ddl-auto está en "validate", por lo
 * que esta clase debe coincidir exactamente con esa tabla.
 * Un {@link Componente} referencia una Categoria vía la FK categoria_id.
 */
@Entity
@Table(name = "categorias")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
}