package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeudaDTO {

    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    private Double montoTotal; // Para la distribución masiva proporcional

    @NotNull
    private LocalDate fecha;

    private List<Long> puestoIds;

    @NotNull
    private Long conceptoId;
}
