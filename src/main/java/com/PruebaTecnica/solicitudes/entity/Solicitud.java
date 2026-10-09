package com.PruebaTecnica.solicitudes.entity;

import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.enums.MotivoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteId;

    @Column(nullable = false, length = 4)
    private String ultimosCuatroDigitos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoSolicitud motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    private String motivoRechazo;

    public Solicitud() {
    }

    // Getters y setters
        public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
