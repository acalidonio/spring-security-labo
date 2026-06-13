package com.server.app.controllers;

import com.server.app.dto.finanzas.InversionCreateDto;
import com.server.app.dto.finanzas.PortafolioCreateDto;
import com.server.app.dto.finanzas.RendimientoResponseDto;
import com.server.app.dto.response.Pagination;
import com.server.app.dto.response.PaginationMeta;
import com.server.app.entities.Activo;
import com.server.app.entities.Inversion;
import com.server.app.entities.Portafolio;
import com.server.app.entities.User;
import com.server.app.services.FinanzasService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finanzas")
@AllArgsConstructor
public class FinanzasController {

    private final FinanzasService finanzasService;

    @GetMapping("/portafolios")
    public ResponseEntity<Pagination<Portafolio>> getPortafolios(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Portafolio> p = finanzasService.getPortafolios(user, page, size);
        return ResponseEntity.ok(new Pagination<>(
                p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements())
        ));
    }

    @PostMapping("/portafolios")
    public ResponseEntity<Portafolio> createPortafolio(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PortafolioCreateDto dto) {
        return ResponseEntity.ok(finanzasService.createPortafolio(user, dto));
    }

    @GetMapping("/activos")
    public ResponseEntity<Pagination<Activo>> getActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Activo> p = finanzasService.getActivos(page, size);
        return ResponseEntity.ok(new Pagination<>(
                p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements())
        ));
    }

    @PostMapping("/inversiones")
    public ResponseEntity<Inversion> registrarInversion(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody InversionCreateDto dto) {
        return ResponseEntity.ok(finanzasService.registrarInversion(user, dto));
    }

    @GetMapping("/portafolios/{id}/rendimiento")
    public ResponseEntity<RendimientoResponseDto> calcularRendimiento(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(finanzasService.calcularRendimiento(id, user));
    }
}
