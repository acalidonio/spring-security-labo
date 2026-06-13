package com.server.app.dto.finanzas;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RendimientoResponseDto {
    private Long portafolioId;
    private String nombrePortafolio;
    private BigDecimal rendimientoAbsoluto;
    private BigDecimal rendimientoPorcentual;
}
