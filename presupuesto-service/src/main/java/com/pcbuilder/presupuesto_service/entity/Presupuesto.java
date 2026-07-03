package com.pcbuilder.presupuesto_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "presupuestos")
@Data
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El total aprobado es obligatorio")
    @Min(value = 0, message = "El total aprobado no puede ser menor a 0")
    @Column(name = "total_aprobado", nullable = false)
    private Integer totalAprobado;

    @NotNull(message = "El total gastado es obligatorio")
    @Min(value = 0, message = "El total gastado no puede ser menor a 0")
    @Column(name = "total_gastado", nullable = false)
    private Integer totalGastado;

    @NotNull(message = "La fecha de registro es obligatoria")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 50)
    private String estado;
}
