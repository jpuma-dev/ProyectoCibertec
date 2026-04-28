package com.cibertec.proyecto.dtos;

import com.cibertec.proyecto.enums.EstadoDeuda;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeudaResponseDTO {
    private Long id;
    private Double monto;
    private LocalDate fecha;
    private EstadoDeuda estado;
    private String conceptoNombre;
    private List<String> puestoNumeros;
}
