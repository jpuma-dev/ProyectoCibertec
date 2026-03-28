package com.cibertec.proyecto.entity;

import com.cibertec.proyecto.enums.EstadoDeuda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deudas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoDeuda estado;

    @ManyToMany
    @JoinTable(
            name = "deuda_puesto",
            joinColumns = @JoinColumn(name = "deuda_id"),
            inverseJoinColumns = @JoinColumn(name = "puesto_id")
    )
    private List<Puesto> puestos;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concepto_id")
    private ConceptoDeuda concepto;

    @OneToMany(mappedBy = "deuda")
    private List<Pago> pagos;
}
