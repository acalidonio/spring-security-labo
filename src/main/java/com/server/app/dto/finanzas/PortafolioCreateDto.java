package com.server.app.dto.finanzas;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortafolioCreateDto {
    @NotBlank(message = "El nombre del portafolio no puede estar vacío")
    private String nombre;

    @NotNull(message = "El balance total es requerido")
    @DecimalMin(value = "0.0", message = "El balance no puede ser negativo")
    private BigDecimal balanceTotal;

    @NotBlank(message = "El perfil de riesgo es requerido")
    private String riesgoPerfil;
}
