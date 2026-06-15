package com.cibertec.proyecto.dtos;

public record SocioResponseDTO(
    Long id,
    String nombre,
    String apellido,
    String dni,
    String telefono,
    String email
) {}
