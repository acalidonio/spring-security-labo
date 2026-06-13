package com.server.app.dto.finanzas;

import com.server.app.entities.EstadoInversion;
import com.server.app.entities.TipoOperacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InversionCreateDto {
    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    @NotNull(message = "El precio de compra/venta es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "El tipo de operación es requerido")
    private TipoOperacion tipoOperacion;

    @NotNull(message = "El estado de la inversión es requerido")
    private EstadoInversion estado;

    @NotNull(message = "El ID del portafolio es requerido")
    private Long portafolioId;

    @NotNull(message = "El ID del activo es requerido")
    private Long activoId;
}
