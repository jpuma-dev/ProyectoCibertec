package com.cibertec.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

// ══════════════════════════════════════════════════════════
// CORRECCIÓN: Se quitaron @Getter y @Setter redundantes
// cuando ya existe @Data (que los incluye).
// ══════════════════════════════════════════════════════════

@Entity
@Table(name = "socios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @Column(length = 9)
    private String telefono;

    @Column(length = 100)
    private String email;

    @OneToMany(mappedBy = "socio", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Puesto> puestos;
}
