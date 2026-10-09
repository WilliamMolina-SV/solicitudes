package com.PruebaTecnica.solicitudes.mapper;

import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.entity.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public Solicitud toEntity(SolicitudRequest request) {
        Solicitud solicitud = new Solicitud();

        solicitud.setClienteId(request.getClienteId());
        solicitud.setUltimosCuatroDigitos(request.getUltimosCuatroDigitos());
        solicitud.setMotivo(request.getMotivo());

        return solicitud;
    }

    public SolicitudResponse toResponse(Solicitud solicitud) {
        SolicitudResponse response = new SolicitudResponse();

        response.setId(solicitud.getId());
        response.setClienteId(solicitud.getClienteId());
        response.setUltimosCuatroDigitos(solicitud.getUltimosCuatroDigitos());
        response.setMotivo(solicitud.getMotivo());
        response.setEstado(solicitud.getEstado());
        response.setFechaCreacion(solicitud.getFechaCreacion());
        response.setFechaActualizacion(solicitud.getFechaActualizacion());
        response.setMotivoRechazo(solicitud.getMotivoRechazo());

        return response;
    }
}
