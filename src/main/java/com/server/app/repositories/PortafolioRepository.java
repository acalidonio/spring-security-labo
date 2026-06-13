package com.server.app.repositories;

import com.server.app.entities.Portafolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortafolioRepository extends JpaRepository<Portafolio, Long> {
    Page<Portafolio> findByUsuarioId(Integer usuarioId, Pageable pageable);
}
