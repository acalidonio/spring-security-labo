package com.server.app.services;

import com.server.app.dto.finanzas.InversionCreateDto;
import com.server.app.dto.finanzas.PortafolioCreateDto;
import com.server.app.dto.finanzas.RendimientoResponseDto;
import com.server.app.entities.*;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.ActivoRepository;
import com.server.app.repositories.InversionRepository;
import com.server.app.repositories.PortafolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanzasService {

    private final PortafolioRepository portafolioRepository;
    private final ActivoRepository activoRepository;
    private final InversionRepository inversionRepository;

    @Transactional(readOnly = true)
    public Page<Portafolio> getPortafolios(User user, int page, int size) {
        return portafolioRepository.findByUsuarioId(user.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public Portafolio createPortafolio(User user, PortafolioCreateDto dto) {
        Portafolio portafolio = Portafolio.builder()
                .nombre(dto.getNombre())
                .riesgoPerfil(dto.getRiesgoPerfil())
                .balanceTotal(dto.getBalanceTotal())
                .usuario(user)
                .build();
        return portafolioRepository.save(portafolio);
    }

    @Transactional(readOnly = true)
    public Page<Activo> getActivos(int page, int size) {
        return activoRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Inversion registrarInversion(User user, InversionCreateDto dto) {
        Portafolio portafolio = portafolioRepository.findById(dto.getPortafolioId())
                .orElseThrow(() -> new NotFoundException("Portafolio no encontrado"));

        if (portafolio.getUsuario().getId() != user.getId()) {
            throw new ForbiddenException("No tienes permisos para operar en este portafolio");
        }

        Activo activo = activoRepository.findById(dto.getActivoId())
                .orElseThrow(() -> new NotFoundException("Activo no encontrado"));

        BigDecimal costoTotal = dto.getCantidad().multiply(dto.getPrecio());

        if (dto.getTipoOperacion() == TipoOperacion.COMPRA) {
            if (portafolio.getBalanceTotal().compareTo(costoTotal) < 0) {
                throw new BadRequestException("Fondos insuficientes en el portafolio para realizar la compra");
            }
            portafolio.setBalanceTotal(portafolio.getBalanceTotal().subtract(costoTotal));
        } else if (dto.getTipoOperacion() == TipoOperacion.VENTA) {
            portafolio.setBalanceTotal(portafolio.getBalanceTotal().add(costoTotal));
        }

        portafolioRepository.save(portafolio);

        Inversion inversion = Inversion.builder()
                .cantidad(dto.getCantidad())
                .precioCompra(dto.getPrecio())
                .fecha(LocalDateTime.now())
                .tipoOperacion(dto.getTipoOperacion())
                .estado(dto.getEstado())
                .portafolio(portafolio)
                .activo(activo)
                .build();

        return inversionRepository.save(inversion);
    }

    @Transactional(readOnly = true)
    public RendimientoResponseDto calcularRendimiento(Long portafolioId, User user) {
        Portafolio portafolio = portafolioRepository.findById(portafolioId)
                .orElseThrow(() -> new NotFoundException("Portafolio no encontrado"));

        if (portafolio.getUsuario().getId() != user.getId()) {
            throw new ForbiddenException("No tienes acceso a este portafolio");
        }

        List<Inversion> inversionesAbiertas = inversionRepository
                .findByPortafolioIdAndEstado(portafolioId, EstadoInversion.ABIERTA);

        BigDecimal valorCompraTotal = BigDecimal.ZERO;
        BigDecimal valorMercadoTotal = BigDecimal.ZERO;

        for (Inversion inv : inversionesAbiertas) {
            if (inv.getTipoOperacion() == TipoOperacion.COMPRA) {
                BigDecimal costo = inv.getCantidad().multiply(inv.getPrecioCompra());
                BigDecimal mercado = inv.getCantidad().multiply(inv.getActivo().getPrecioMercado());

                valorCompraTotal = valorCompraTotal.add(costo);
                valorMercadoTotal = valorMercadoTotal.add(mercado);
            }
        }

        BigDecimal rendimientoAbsoluto = valorMercadoTotal.subtract(valorCompraTotal);
        BigDecimal rendimientoPorcentual = BigDecimal.ZERO;

        if (valorCompraTotal.compareTo(BigDecimal.ZERO) > 0) {
            rendimientoPorcentual = rendimientoAbsoluto
                    .divide(valorCompraTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return new RendimientoResponseDto(
                portafolio.getId(),
                portafolio.getNombre(),
                rendimientoAbsoluto,
                rendimientoPorcentual
        );
    }
}
