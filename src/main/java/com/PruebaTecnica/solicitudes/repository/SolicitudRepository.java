package com.PruebaTecnica.solicitudes.repository;

import com.PruebaTecnica.solicitudes.entity.Solicitud;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Collection;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    boolean existsByClienteIdAndUltimosCuatroDigitosAndEstadoIn(
            String clienteId,
            String ultimosCuatroDigitos,
            Collection<EstadoSolicitud> estados
    );

    List<Solicitud> findByEstado(EstadoSolicitud estado);
}