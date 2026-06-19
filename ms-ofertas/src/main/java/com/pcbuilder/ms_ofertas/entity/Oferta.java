package com.pcbuilder.ms_ofertas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "ofertas")
@Data
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vo' soy vio, el código del cupón no puede ir en blanco")
    @Column(unique = true)
    private String codigo;

    @NotNull(message = "Póngale el porcentaje de descuento po rey")
    @Min(value = 1, message = "Mínimo 1% de descuento, no seai cagao")
    @Max(value = 100, message = "¡Te fuiste al chancho! Máximo 100% de descuento")
    private Integer porcentajeDescuento;

    private boolean activa = true;
}