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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.service.SolicitudService;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Solicitudes", description = "API para gestionar solicitudes de reposición de tarjetas")
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Operation(summary = "Crear una solicitud", description = "Registra una nueva solicitud de reposición de tarjeta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una solicitud activa para el cliente y tarjeta")
    })
    @PostMapping
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody SolicitudRequest request) {

        SolicitudResponse response = solicitudService.crearSolicitud(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Obtener una solicitud", description = "Obtiene una solicitud de reposición mediante su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerSolicitud(
            @PathVariable Long id) {

        SolicitudResponse response = solicitudService.obtenerSolicitud(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar solicitudes", description = "Obtiene todas las solicitudes o las filtra por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listarSolicitudes(
            @Parameter(description = "Estado por el cual filtrar las solicitudes", required = false) @RequestParam(required = false) EstadoSolicitud estado) {

        List<SolicitudResponse> response;

        if (estado == null) {
            response = solicitudService.listarSolicitudes();
        } else {
            response = solicitudService.listarPorEstado(estado);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado de una solicitud", description = "Actualiza el estado de una solicitud respetando las transiciones permitidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "409", description = "Transición de estado no permitida o conflicto de negocio")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequest request) {

        SolicitudResponse response = solicitudService.cambiarEstado(id, request);

        return ResponseEntity.ok(response);
    }
}
