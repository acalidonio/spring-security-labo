package com.server.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "activos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String simbolo;

    @Column(nullable = false)
    private String mercado;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal precioMercado;

    @Column
    private String sector;
}
