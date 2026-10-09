package com.PruebaTecnica.solicitudes.dto;

import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class CambioEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoSolicitud nuevoEstado;

    private String motivoRechazo;

    public CambioEstadoRequest() {
    }

    public EstadoSolicitud getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoSolicitud nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}