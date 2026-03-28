package com.cibertec.proyecto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "puestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio;

    @ManyToMany(mappedBy = "puestos")
    private List<Deuda> deudas;
}