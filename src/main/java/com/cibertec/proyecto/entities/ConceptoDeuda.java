package com.cibertec.proyecto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "conceptos_deuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConceptoDeuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private boolean recurrente = false;

    private Double montoSugerido;

    @JsonIgnore
    @OneToMany(mappedBy = "concepto", fetch = FetchType.LAZY)
    private List<Deuda> deudas;
}
