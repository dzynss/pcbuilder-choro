package com.pcbuilder.ms_cotizaciones.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa una cotización (pedido de un componente para un usuario).
 * Mapeada a la tabla "cotizaciones" cuyo esquema real vive en Liquibase
 * (db.changelog-master.xml); ddl-auto=validate, por lo que esta clase debe coincidir
 * exactamente con esa tabla. Los getters/setters los genera Lombok (@Data).
 */
@Entity
@Table(name = "cotizaciones")
@Data
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;      // referencia al usuario en ms-usuarios (validado vía UsuarioClient)
    private Long idComponente;   // referencia al componente en ms-componentes (validado vía ComponenteClient)
    private Integer cantidad;
    private Double total;        // calculado por CotizacionService con el precio real del componente, nunca confiado del request
}
