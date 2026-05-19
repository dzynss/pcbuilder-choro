package com.pcbuilder.ms_resenas.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private Long idComponente; // ID de la pieza en el otro microservicio
}