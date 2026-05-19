package com.pcbuilder.ms_cotizaciones.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cotizaciones")
@Data
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long idUsuario;      // viene del ms-usuarios
    private Long idComponente;   // viene del ms-componentes
    private Integer cantidad;
    private Double total;        // este lo calculamos nosotros
}
