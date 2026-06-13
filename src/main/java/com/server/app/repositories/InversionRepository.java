package com.server.app.repositories;

import com.server.app.entities.EstadoInversion;
import com.server.app.entities.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InversionRepository extends JpaRepository<Inversion, Long> {
    List<Inversion> findByPortafolioIdAndEstado(Long portafolioId, EstadoInversion estado);
}
