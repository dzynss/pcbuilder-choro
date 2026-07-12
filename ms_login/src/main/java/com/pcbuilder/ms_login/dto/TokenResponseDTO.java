package com.pcbuilder.ms_login.dto;

/**
 * DTO de respuesta de un login exitoso: contiene el JWT generado por
 * {@link com.pcbuilder.ms_login.util.JwtUtil#generarToken}. Devuelto por
 * {@link com.pcbuilder.ms_login.controller.AuthController#login}.
 */
public record TokenResponseDTO(String token) {}
