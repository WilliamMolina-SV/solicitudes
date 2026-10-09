package com.PruebaTecnica.solicitudes.controller;

import com.PruebaTecnica.solicitudes.dto.CambioEstadoRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudRequest;
import com.PruebaTecnica.solicitudes.dto.SolicitudResponse;
import com.PruebaTecnica.solicitudes.enums.EstadoSolicitud;
import com.PruebaTecnica.solicitudes.enums.MotivoSolicitud;
import com.PruebaTecnica.solicitudes.exception.SolicitudConflictException;
import com.PruebaTecnica.solicitudes.exception.SolicitudNoEncontradaException;
import com.PruebaTecnica.solicitudes.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@WebMvcTest(SolicitudController.class)
public class SolicitudControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private SolicitudService solicitudService;

        @Test
        void debeRechazarUltimosCuatroDigitosInvalidos() throws Exception {

                String requestJson = """
                                {
                                    "clienteId": "CLI-10025",
                                    "ultimosCuatroDigitos": "123",
                                    "motivo": "PERDIDA"
                                }
                                """;

                mockMvc.perform(
                                post("/api/solicitudes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message").value(
                                                "ultimosCuatroDigitos: Los últimos cuatro dígitos deben contener exactamente cuatro números"));
        }

        @Test
        void debeRechazarClienteIdVacio() throws Exception {

                String requestJson = """
                                {
                                    "clienteId": "",
                                    "ultimosCuatroDigitos": "1234",
                                    "motivo": "PERDIDA"
                                }
                                """;

                mockMvc.perform(
                                post("/api/solicitudes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void debeCrearSolicitudYRetornar201() throws Exception {

                SolicitudResponse response = new SolicitudResponse();
                response.setId(1L);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudService.crearSolicitud(any(SolicitudRequest.class)))
                                .thenReturn(response);

                String requestJson = """
                                {
                                    "clienteId": "CLI-10025",
                                    "ultimosCuatroDigitos": "4589",
                                    "motivo": "PERDIDA"
                                }
                                """;

                mockMvc.perform(
                                post("/api/solicitudes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isCreated());
        }

        @Test
        void debeObtenerSolicitudPorIdYRetornar200() throws Exception {

                SolicitudResponse response = new SolicitudResponse();
                response.setId(1L);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudService.obtenerSolicitud(1L))
                                .thenReturn(response);

                mockMvc.perform(
                                get("/api/solicitudes/1"))
                                .andExpect(status().isOk());
        }

        @Test
        void debeRetornar404CuandoSolicitudNoExiste() throws Exception {

                when(solicitudService.obtenerSolicitud(999L))
                                .thenThrow(new SolicitudNoEncontradaException(999L));

                mockMvc.perform(
                                get("/api/solicitudes/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void debeListarSolicitudesYRetornar200() throws Exception {

                SolicitudResponse response = new SolicitudResponse();
                response.setId(1L);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudService.listarSolicitudes())
                                .thenReturn(List.of(response));

                mockMvc.perform(
                                get("/api/solicitudes"))
                                .andExpect(status().isOk());
        }

        @Test
        void debeListarSolicitudesPorEstado() throws Exception {

                SolicitudResponse response = new SolicitudResponse();
                response.setId(1L);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.RECIBIDA);

                when(solicitudService.listarPorEstado(EstadoSolicitud.RECIBIDA))
                                .thenReturn(List.of(response));

                mockMvc.perform(
                                get("/api/solicitudes")
                                                .param("estado", "RECIBIDA"))
                                .andExpect(status().isOk());
        }

        @Test
        void debeCambiarEstadoYRetornar200() throws Exception {

                SolicitudResponse response = new SolicitudResponse();
                response.setId(1L);
                response.setClienteId("CLI-10025");
                response.setUltimosCuatroDigitos("4589");
                response.setMotivo(MotivoSolicitud.PERDIDA);
                response.setEstado(EstadoSolicitud.EN_PROCESO);

                when(solicitudService.cambiarEstado(
                                eq(1L),
                                any(CambioEstadoRequest.class))).thenReturn(response);

                String requestJson = """
                                {
                                    "nuevoEstado": "EN_PROCESO"
                                }
                                """;

                mockMvc.perform(
                                patch("/api/solicitudes/1/estado")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isOk());
        }

        @Test
        void debeRetornar409CuandoLaTransicionNoEsValida() throws Exception {

                when(solicitudService.cambiarEstado(
                                eq(1L),
                                any(CambioEstadoRequest.class))).thenThrow(
                                                new SolicitudConflictException(
                                                                "No se permite cambiar el estado de RECIBIDA a COMPLETADA"));

                String requestJson = """
                                {
                                    "nuevoEstado": "COMPLETADA"
                                }
                                """;

                mockMvc.perform(
                                patch("/api/solicitudes/1/estado")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isConflict());
        }

        @Test
        void debeRechazarCambioEstadoSinNuevoEstado() throws Exception {

                mockMvc.perform(
                                patch("/api/solicitudes/1/estado")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                    {
                                                                        "motivoRechazo": "Motivo de prueba"
                                                                    }
                                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("nuevoEstado: El nuevo estado es obligatorio"));
        }
}
