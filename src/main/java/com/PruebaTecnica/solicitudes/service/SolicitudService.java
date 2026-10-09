package com.PruebaTecnica.solicitudes.service;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import java.util.List;

public interface SolicitudService {

    SolicitudResponse crearSolicitud(SolicitudRequest request);

    SolicitudResponse obtenerSolicitud(Long id);

    List<SolicitudResponse> listarSolicitudes();

    List<SolicitudResponse> listarPorEstado(EstadoSolicitud estado);

    SolicitudResponse cambiarEstado(Long id, CambioEstadoRequest request);
}