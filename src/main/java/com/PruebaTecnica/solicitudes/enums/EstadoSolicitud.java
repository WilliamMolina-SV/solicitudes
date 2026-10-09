package com.PruebaTecnica.solicitudes.enums;

public enum EstadoSolicitud {

    RECIBIDA,
    EN_PROCESO,
    COMPLETADA,
    RECHAZADA;

    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado){
        return switch (this) {
            case RECIBIDA ->
                    nuevoEstado == EN_PROCESO ||
                    nuevoEstado == RECHAZADA;

            case EN_PROCESO ->
                    nuevoEstado == COMPLETADA ||
                    nuevoEstado == RECHAZADA;

            case COMPLETADA, RECHAZADA ->
                    false;
        };
    }
}
