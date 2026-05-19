package com.pcbuilder.ms_usuarios.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    @Column(unique = true)
    private String correo;
    private String password;
    private String rol; // ej.: "ADMIN" o "USER" (juan te meo)
}