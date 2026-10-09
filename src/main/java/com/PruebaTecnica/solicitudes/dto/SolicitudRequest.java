package com.PruebaTecnica.solicitudes.dto;

import com.PruebaTecnica.solicitudes.enums.MotivoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SolicitudRequest {

    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;

    @NotBlank(message = "Los últimos cuatro dígitos son obligatorios")
    @Pattern(
            regexp = "\\d{4}",
            message = "Los últimos cuatro dígitos deben contener exactamente cuatro números"
    )
    private String ultimosCuatroDigitos;

    @NotNull(message = "El motivo es obligatorio")
    private MotivoSolicitud motivo;

    public SolicitudRequest() {
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getUltimosCuatroDigitos() {
        return ultimosCuatroDigitos;
    }

    public void setUltimosCuatroDigitos(String ultimosCuatroDigitos) {
        this.ultimosCuatroDigitos = ultimosCuatroDigitos;
    }

    public MotivoSolicitud getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoSolicitud motivo) {
        this.motivo = motivo;
    }
}
