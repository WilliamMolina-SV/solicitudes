package com.PruebaTecnica.solicitudes.service;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.entity.Solicitud;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.mapper.SolicitudMapper;
import com.PruebaTecnica.solicitudes.repository.SolicitudRepository;
import com.PruebaTecnica.solicitudes.exception.SolicitudConflictException;
import com.PruebaTecnica.solicitudes.exception.SolicitudNoEncontradaException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper solicitudMapper;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository, SolicitudMapper solicitudMapper) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudMapper = solicitudMapper;
    }

    @Override
    public SolicitudResponse cambiarEstado(Long id, CambioEstadoRequest request) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new SolicitudNoEncontradaException(id));

        if (!solicitud.getEstado().puedeTransicionarA(request.getNuevoEstado())) {
            throw new SolicitudConflictException(
                    "No se permite cambiar el estado de "
                            + solicitud.getEstado()
                            + " a "
                            + request.getNuevoEstado());
        }

        if (request.getNuevoEstado() == EstadoSolicitud.RECHAZADA
                && (request.getMotivoRechazo() == null
                        || request.getMotivoRechazo().isBlank())) {

            throw new SolicitudConflictException(
                    "El motivo de rechazo es obligatorio");
        }

        solicitud.setEstado(request.getNuevoEstado());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        if (request.getNuevoEstado() == EstadoSolicitud.RECHAZADA) {
            solicitud.setMotivoRechazo(request.getMotivoRechazo());
        }

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);

        return solicitudMapper.toResponse(solicitudActualizada);
    }

    @Override
    public SolicitudResponse crearSolicitud(SolicitudRequest request) {

        List<EstadoSolicitud> estadosActivos = List.of(
                EstadoSolicitud.RECIBIDA,
                EstadoSolicitud.EN_PROCESO);

        boolean existeSolicitudActiva = solicitudRepository.existsByClienteIdAndUltimosCuatroDigitosAndEstadoIn(
                request.getClienteId(),
                request.getUltimosCuatroDigitos(),
                estadosActivos);

        if (existeSolicitudActiva) {
            throw new SolicitudConflictException(
                    "Ya existe una solicitud activa para el cliente y la tarjeta indicados");
        }

        Solicitud solicitud = solicitudMapper.toEntity(request);

        LocalDateTime ahora = LocalDateTime.now();

        solicitud.setEstado(EstadoSolicitud.RECIBIDA);
        solicitud.setFechaCreacion(ahora);
        solicitud.setFechaActualizacion(ahora);

        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        return solicitudMapper.toResponse(solicitudGuardada);
    }

    @Override
    public List<SolicitudResponse> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado)
                .stream()
                .map(solicitudMapper::toResponse)
                .toList();
    }

    @Override
    public List<SolicitudResponse> listarSolicitudes() {
        return solicitudRepository.findAll()
                .stream()
                .map(solicitudMapper::toResponse)
                .toList();

    }

    @Override
    public SolicitudResponse obtenerSolicitud(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new SolicitudNoEncontradaException(id));

        return solicitudMapper.toResponse(solicitud);
    }
}