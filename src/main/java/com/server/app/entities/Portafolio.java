package com.server.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "portafolios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceTotal;

    @Column(nullable = false)
    private String riesgoPerfil;
}
