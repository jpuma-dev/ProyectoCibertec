package com.cibertec.proyecto.entities;

import com.cibertec.proyecto.enums.EstadoDeuda;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deudas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDeuda estado;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "deuda_puesto",
            joinColumns = @JoinColumn(name = "deuda_id"),
            inverseJoinColumns = @JoinColumn(name = "puesto_id")
    )
    private List<Puesto> puestos;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "concepto_id")
    private ConceptoDeuda concepto;

    @JsonIgnore
    @OneToMany(mappedBy = "deuda", fetch = FetchType.LAZY)
    private List<Pago> pagos;
}
