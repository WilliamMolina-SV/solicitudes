package com.PruebaTecnica.solicitudes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.entity.Solicitud;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.enums.MotivoSolicitud;
import com.PruebaTecnica.solicitudes.exception.SolicitudConflictException;
import com.PruebaTecnica.solicitudes.exception.SolicitudNoEncontradaException;
import com.PruebaTecnica.solicitudes.mapper.SolicitudMapper;
import com.PruebaTecnica.solicitudes.repository.SolicitudRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolicitudServiceImplTest {

        @Mock
        private SolicitudRepository solicitudRepository;

        @Mock
        private SolicitudMapper solicitudMapper;

        @InjectMocks
        private SolicitudServiceImpl solicitudService;

        @Test
        void debeCrearSolicitudCorrectamente() {

                SolicitudRequest request = new SolicitudRequest();
                request.setClienteId("CLI-10025");
                request.setUltimosCuatroDigitos("4589");
                request.setMotivo(MotivoSolicitud.PERDIDA);

                Solicitud solicitud = new Solicitud();

                SolicitudResponse response = new SolicitudResponse();
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudRepository
                                .existsByClienteIdAndUltimosCuatroDigitosAndEstadoIn(
                                                eq("CLI-10025"),
                                                eq("4589"),
                                                anyList()))
                                .thenReturn(false);

                when(solicitudMapper.toEntity(request))
                                .thenReturn(solicitud);

                when(solicitudRepository.save(solicitud))
                                .thenReturn(solicitud);

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(response);

                SolicitudResponse resultado = solicitudService.crearSolicitud(request);

                assertNotNull(resultado);
                assertEquals("CLI-10025", resultado.getClienteId());
                assertEquals("4589", resultado.getUltimosCuatroDigitos());
                assertEquals(MotivoSolicitud.PERDIDA, resultado.getMotivo());
                assertEquals(EstadoSolicitud.RECIBIDA, resultado.getEstado());

                verify(solicitudRepository).save(solicitud);
                verify(solicitudMapper).toEntity(request);
                verify(solicitudMapper).toResponse(solicitud);
        }

        @Test
        void noDebePermitirSolicitudActivaDuplicada() {

                SolicitudRequest request = new SolicitudRequest();
                request.setClienteId("CLI-10025");
                request.setUltimosCuatroDigitos("4589");
                request.setMotivo(MotivoSolicitud.PERDIDA);

                when(solicitudRepository
                                .existsByClienteIdAndUltimosCuatroDigitosAndEstadoIn(
                                                eq("CLI-10025"),
                                                eq("4589"),
                                                anyList()))
                                .thenReturn(true);

                assertThrows(
                                SolicitudConflictException.class,
                                () -> solicitudService.crearSolicitud(request));

                verify(solicitudRepository, never()).save(any(Solicitud.class));
                verify(solicitudMapper, never()).toEntity(any(SolicitudRequest.class));
        }

        @Test
        void debeObtenerSolicitudExistente() {

                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setClienteId("CLI-10025");
                solicitud.setUltimosCuatroDigitos("4589");
                solicitud.setMotivo(MotivoSolicitud.PERDIDA);
                solicitud.setEstado(EstadoSolicitud.RECIBIDA);

                SolicitudResponse response = new SolicitudResponse();
                response.setId(id);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(response);

                SolicitudResponse resultado = solicitudService.obtenerSolicitud(id);

                assertNotNull(resultado);
                assertEquals(id, resultado.getId());
                assertEquals("CLI-10025", resultado.getClienteId());
                assertEquals(EstadoSolicitud.RECIBIDA, resultado.getEstado());

                verify(solicitudRepository).findById(id);
                verify(solicitudMapper).toResponse(solicitud);
        }

        @Test
        void debeLanzarExcepcionCuandoSolicitudNoExiste() {

                Long id = 999L;

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.empty());

                assertThrows(
                                SolicitudNoEncontradaException.class,
                                () -> solicitudService.obtenerSolicitud(id));

                verify(solicitudRepository).findById(id);
                verify(solicitudMapper, never()).toResponse(any(Solicitud.class));
        }

        @Test
        void debeCambiarDeRecibidaAEnProceso() {

                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.RECIBIDA);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.EN_PROCESO);

                SolicitudResponse response = new SolicitudResponse();
                response.setId(id);
                response.setEstado(EstadoSolicitud.EN_PROCESO);

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                when(solicitudRepository.save(solicitud))
                                .thenReturn(solicitud);

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(response);

                SolicitudResponse resultado = solicitudService.cambiarEstado(id, request);

                assertNotNull(resultado);
                assertEquals(EstadoSolicitud.EN_PROCESO, resultado.getEstado());

                verify(solicitudRepository).save(solicitud);
                verify(solicitudMapper).toResponse(solicitud);
        }

        @Test
        void debeCambiarDeEnProcesoACompletada() {
                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.EN_PROCESO);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.COMPLETADA);

                SolicitudResponse responseEsperada = new SolicitudResponse();
                responseEsperada.setId(id);
                responseEsperada.setEstado(EstadoSolicitud.COMPLETADA);

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                when(solicitudRepository.save(any(Solicitud.class)))
                                .thenReturn(solicitud);

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(responseEsperada);

                SolicitudResponse resultado = solicitudService.cambiarEstado(id, request);

                assertNotNull(resultado);
                assertEquals(
                                EstadoSolicitud.COMPLETADA,
                                resultado.getEstado());

                verify(solicitudRepository).save(solicitud);
                verify(solicitudMapper).toResponse(solicitud);
        }

        @Test
        void noDebePermitirTransicionInvalida() {

                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.RECIBIDA);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.COMPLETADA);

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                assertThrows(
                                SolicitudConflictException.class,
                                () -> solicitudService.cambiarEstado(id, request));

                verify(solicitudRepository, never()).save(any(Solicitud.class));
        }

        @Test
        void noDebePermitirRechazoSinMotivo() {

                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.RECIBIDA);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.RECHAZADA);
                request.setMotivoRechazo(null);

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                assertThrows(
                                SolicitudConflictException.class,
                                () -> solicitudService.cambiarEstado(id, request));

                verify(solicitudRepository, never()).save(any(Solicitud.class));
        }

        @Test
        void debePermitirRechazoConMotivo() {
                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.RECIBIDA);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.RECHAZADA);
                request.setMotivoRechazo("La tarjeta ya fue reemplazada");

                SolicitudResponse responseEsperada = new SolicitudResponse();
                responseEsperada.setId(id);
                responseEsperada.setEstado(EstadoSolicitud.RECHAZADA);
                responseEsperada.setMotivoRechazo("La tarjeta ya fue reemplazada");

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                when(solicitudRepository.save(any(Solicitud.class)))
                                .thenReturn(solicitud);

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(responseEsperada);

                SolicitudResponse resultado = solicitudService.cambiarEstado(id, request);

                assertNotNull(resultado);
                assertEquals(EstadoSolicitud.RECHAZADA, resultado.getEstado());
                assertEquals(
                                "La tarjeta ya fue reemplazada",
                                resultado.getMotivoRechazo());

                verify(solicitudRepository).save(solicitud);
                verify(solicitudMapper).toResponse(solicitud);
        }

        @Test
        void debePermitirRechazoDesdeEnProcesoConMotivo() {
                Long id = 1L;

                Solicitud solicitud = new Solicitud();
                solicitud.setId(id);
                solicitud.setEstado(EstadoSolicitud.EN_PROCESO);

                CambioEstadoRequest request = new CambioEstadoRequest();
                request.setNuevoEstado(EstadoSolicitud.RECHAZADA);
                request.setMotivoRechazo("La tarjeta ya fue reemplazada");

                SolicitudResponse responseEsperada = new SolicitudResponse();
                responseEsperada.setId(id);
                responseEsperada.setEstado(EstadoSolicitud.RECHAZADA);
                responseEsperada.setMotivoRechazo("La tarjeta ya fue reemplazada");

                when(solicitudRepository.findById(id))
                                .thenReturn(java.util.Optional.of(solicitud));

                when(solicitudRepository.save(any(Solicitud.class)))
                                .thenReturn(solicitud);

                when(solicitudMapper.toResponse(solicitud))
                                .thenReturn(responseEsperada);

                SolicitudResponse resultado = solicitudService.cambiarEstado(id, request);

                assertNotNull(resultado);
                assertEquals(
                                EstadoSolicitud.RECHAZADA,
                                resultado.getEstado());
                assertEquals(
                                "La tarjeta ya fue reemplazada",
                                resultado.getMotivoRechazo());

                verify(solicitudRepository).save(solicitud);
                verify(solicitudMapper).toResponse(solicitud);
        }
}
