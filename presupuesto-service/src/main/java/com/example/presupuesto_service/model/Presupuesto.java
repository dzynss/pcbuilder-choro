package com.example.presupuesto_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El total aprobado es obligatorio")
    @Min(value = 0, message = "El total aprobado no puede ser menor a 0")
    @Column(name = "total_aprobado", nullable = false)
    private int totalAprobado;

    @NotNull(message = "El total gastado es obligatorio")
    @Min(value = 0, message = "El total gastado no puede ser menor a 0")
    @Column(name = "total_gastado", nullable = false)
    private int totalGastado;

    @NotNull(message = "La fecha de registro es obligatoria")
    @Column(name = "fecha_registro", nullable = false)
    private Date fechaRegistro;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 50)
    private String estado;
}