package com.cibertec.proyecto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeudaDTO {

    @NotNull
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private List<Long> puestoIds;

    @NotNull
    private Long conceptoId;
}