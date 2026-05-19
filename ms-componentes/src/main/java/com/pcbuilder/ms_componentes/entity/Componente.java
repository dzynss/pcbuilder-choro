package com.pcbuilder.ms_componentes.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    // esta wea conecta la otra tabla, colta
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}