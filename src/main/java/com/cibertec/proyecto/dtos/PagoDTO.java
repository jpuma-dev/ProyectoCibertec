package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PagoDTO {

    @NotNull
    @Positive
    private Double monto;

    @NotBlank
    private String metodoPago;

    @NotNull
    private Long deudaId;
}
