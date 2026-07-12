package com.pcbuilder.ms_componentes.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa un componente/pieza de PC del catálogo (ej. una CPU).
 * Se mapea a la tabla "componentes", cuyo esquema real viene de Liquibase
 * (db.changelog-master.xml); ddl-auto es "validate", así que la mapea tal cual.
 * El precio y stock aquí son la fuente de verdad que ms_cotizaciones consulta
 * vía Feign (ComponenteResponseDTO) para calcular el total real de una cotización,
 * en vez de confiar en el precio enviado por el cliente; ms-resenas y ms-soporte
 * también consultan este componente para validar referencias.
 */
@Entity
@Table(name = "componentes")
@Data
public class Componente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String marca;
    private Double precio;
    private Integer stock;

    // Relación N:1 hacia la categoría del componente (FK categoria_id).
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}