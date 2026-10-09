package com.PruebaTecnica.solicitudes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.service.SolicitudService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody SolicitudRequest request) {

        SolicitudResponse response = solicitudService.crearSolicitud(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerSolicitud(
            @PathVariable Long id) {

        SolicitudResponse response = solicitudService.obtenerSolicitud(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado) {

        List<SolicitudResponse> response;

        if (estado == null) {
            response = solicitudService.listarSolicitudes();
        } else {
            response = solicitudService.listarPorEstado(estado);
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequest request) {

        SolicitudResponse response = solicitudService.cambiarEstado(id, request);

        return ResponseEntity.ok(response);
    }
}
