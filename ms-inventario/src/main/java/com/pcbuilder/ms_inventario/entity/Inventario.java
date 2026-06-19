package com.pcbuilder.ms_inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Vo' soy vio, el ID del componente no puede ir vacío")
    private Long idComponente;

    @NotNull(message = "Tenís que poner una cantidad")
    @Min(value = 0, message = "El stock no puede ser negativo, no seai pajarón")
    private Integer cantidadDisponible;

    private String ubicacionBodega; // Ej: "Pasillo A, Estante 3"
    
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    public void actualizarFecha() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}