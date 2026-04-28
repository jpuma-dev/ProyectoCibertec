package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeudaDTO {

    private Double monto;
    private Double montoTotal;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El concepto es obligatorio")
    private Long conceptoId;

    private List<Long> puestoIds;
}
