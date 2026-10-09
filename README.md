# API de Solicitudes de Reposición de Tarjetas

Backend REST desarrollado en Java y Spring Boot para gestionar solicitudes de reposición de tarjetas por pérdida, daño o vencimiento.

## 1. Descripción

La aplicación permite registrar y gestionar solicitudes de reposición de tarjetas, aplicando reglas de negocio sobre:

- Validación de datos de entrada.
- Detección de solicitudes activas duplicadas.
- Control del estado de las solicitudes.
- Transiciones de estado permitidas.
- Rechazo de solicitudes con motivo obligatorio.
- Manejo centralizado de errores.
- Persistencia mediante JPA/Hibernate.

La solución utiliza una arquitectura por capas para mantener separadas las responsabilidades y facilitar el mantenimiento y las pruebas automatizadas.

---

## 2. Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- Gradle
- JUnit 5
- Mockito
- Jakarta Bean Validation
- Springdoc OpenAPI / Swagger UI

---

## 3. Requisitos

Para ejecutar el proyecto se requiere:

- Java 17 o superior.
- Git.
- Acceso a terminal.

El proyecto incluye Gradle Wrapper, por lo que no es necesario instalar Gradle de forma independiente.

---

## 4. Arquitectura

La aplicación utiliza una arquitectura por capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / H2
```

### Estructura principal

```text
src/main/java/com/PruebaTecnica/solicitudes/
├── controller/
├── dto/
├── entity/
├── enums/
├── exception/
├── mapper/
├── repository/
├── service/
└── SolicitudesApplication.java
```

### Responsabilidades

**Controller**

Expone los endpoints REST y recibe las solicitudes HTTP.

**Service**

Contiene las reglas de negocio y controla las transiciones de estado.

**Repository**

Gestiona el acceso a los datos mediante Spring Data JPA.

**Entity**

Representa la información persistida en la base de datos.

**DTO**

Define los objetos utilizados para las solicitudes y respuestas de la API.

**Mapper**

Realiza la conversión entre entidades y DTOs.

**Exception**

Contiene las excepciones de negocio y el manejo centralizado de errores.

## 5. Modelo de datos

La entidad `Solicitud` contiene los siguientes campos:

| Campo                  | Tipo            | Descripción                               |
|------------------------|-----------------|-------------------------------------------|
| `id`                   | Long            | Identificador generado por el sistema     |
| `clienteId`            | String          | Identificador del cliente                 |
| `ultimosCuatroDigitos` | String          | Últimos cuatro dígitos de la tarjeta      |
| `motivo`               | MotivoSolicitud | Motivo de la reposición                   |
| `estado`               | EstadoSolicitud | Estado actual de la solicitud             |
| `fechaCreacion`        | LocalDateTime   | Fecha de creación generada por el sistema |
| `fechaActualizacion`   | LocalDateTime   | Fecha de última actualización             |
| `motivoRechazo`        | String          | Motivo del rechazo cuando corresponda     |

### Motivos

Los motivos permitidos son:

```text
PERDIDA
DANIO
VENCIMIENTO
```

### Estados

Los estados permitidos son:

```text
RECIBIDA
EN_PROCESO
COMPLETADA
RECHAZADA
```

## 6. Reglas de negocio

### 6.1 Últimos cuatro dígitos

El campo `ultimosCuatroDigitos` debe contener exactamente cuatro caracteres numéricos.

Ejemplo válido:

```text
4589
```

Ejemplos inválidos:

```text
123
12345
12A4
```

Se utiliza `String` en lugar de un tipo numérico para conservar posibles ceros iniciales.

Ejemplo:

```text
0123
```

### 6.2 Solicitudes activas duplicadas

No se permite más de una solicitud activa para el mismo cliente y la misma tarjeta.

Se consideran estados activos:

```text
RECIBIDA
EN_PROCESO
```

Por lo tanto, si ya existe una solicitud activa para el mismo `clienteId` y `ultimosCuatroDigitos`, una nueva solicitud será rechazada.

### 6.3 Estado inicial

Toda solicitud nueva inicia automáticamente en:

```text
RECIBIDA
```

El estado inicial es controlado por el backend y no es recibido desde el request de creación.

### 6.4 Transiciones de estado

Las transiciones permitidas son:

```text
RECIBIDA
 ├──> EN_PROCESO
 └──> RECHAZADA

EN_PROCESO
 ├──> COMPLETADA
 └──> RECHAZADA
```

Las solicitudes en estado `COMPLETADA` o `RECHAZADA` no pueden modificarse.

### 6.5 Rechazo

Para cambiar una solicitud a `RECHAZADA` es obligatorio proporcionar `motivoRechazo`.

Ejemplo:

```text
{
  "nuevoEstado": "RECHAZADA",
  "motivoRechazo": "La tarjeta ya fue reemplazada"
}
```

## 7. API REST

La API utiliza la siguiente ruta base:

```text
/api/solicitudes
```

### 7.1 Crear solicitud

```text
POST /api/solicitudes
```

Request:

```text
{
  "clienteId": "CLI-10025",
  "ultimosCuatroDigitos": "4589",
  "motivo": "PERDIDA"
}
```

Respuesta exitosa:

```text
HTTP 201 Created
```

Ejemplo:

```text
{
  "id": 1,
  "clienteId": "CLI-10025",
  "ultimosCuatroDigitos": "4589",
  "motivo": "PERDIDA",
  "estado": "RECIBIDA",
  "fechaCreacion": "2026-10-08T20:00:00",
  "fechaActualizacion": "2026-10-08T20:00:00",
  "motivoRechazo": null
}
```

### 7.2 Obtener solicitud por ID

```text
GET /api/solicitudes/{id}
```

Ejemplo:

```text
GET /api/solicitudes/1
```

Respuesta exitosa:

```text
HTTP 200 OK
```

Si la solicitud no existe:

```text
HTTP 404 Not Found
```

### 7.3 Listar solicitudes

```text
GET /api/solicitudes
```

Respuesta:

```text
HTTP 200 OK
```

### 7.4 Filtrar solicitudes por estado

```text
GET /api/solicitudes?estado=RECIBIDA
```

Los estados disponibles son:

```text
RECIBIDA
EN_PROCESO
COMPLETADA
RECHAZADA
```

### 7.5 Cambiar estado

```text
PATCH /api/solicitudes/{id}/estado
```

Ejemplo:

```text
PATCH /api/solicitudes/1/estado
```

Request:

```text
{
  "nuevoEstado": "EN_PROCESO"
}
```

Respuesta exitosa:

```text
HTTP 200 OK
```

Para rechazar:

```text
{
  "nuevoEstado": "RECHAZADA",
  "motivoRechazo": "La tarjeta ya fue reemplazada"
}
```

## 8. Manejo de errores

La aplicación utiliza `@RestControllerAdvice` para centralizar el manejo de excepciones.

### 400 Bad Request

Se utiliza para errores de validación de los datos recibidos.

Ejemplo:

```text
{
  "timestamp": "2026-10-08T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "ultimosCuatroDigitos: Los últimos cuatro dígitos deben contener exactamente cuatro números"
}
```

### 404 Not Found

Se devuelve cuando no existe una solicitud con el ID proporcionado.

Ejemplo:

```text
{
  "timestamp": "2026-10-08T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró la solicitud con id: 999"
}
```

### 409 Conflict

Se utiliza cuando una operación entra en conflicto con las reglas de negocio.

Ejemplos:

- Solicitud activa duplicada.
- Transición de estado no permitida.
- Rechazo sin motivo.

## 9. Configuración de base de datos

Para facilitar la ejecución de la evaluación se utiliza H2 como base de datos relacional en memoria.

Configuración principal:

```text
Motor: H2
Modo: memoria
Base de datos: solicitudesdb
```

La estructura de las tablas es generada automáticamente por Hibernate al iniciar la aplicación.

La aplicación no requiere una base de datos externa para ejecutarse.

## 10. Ejecución del proyecto

### Clonar el repositorio

```text
git clone https://github.com/WilliamMolina-SV/solicitudes.git
```
### Ingresar al proyecto

```text
cd solicitudes
```

### Compilar

```text
./gradlew build
```

### Ejecutar pruebas

```text
./gradlew test
```

### Ejecutar todas las pruebas desde cero

```text
./gradlew clean test
```

### Ejecutar la aplicación

```text
./gradlew bootRun
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

## 11. Pruebas automatizadas

La solución incluye pruebas automatizadas para la lógica de negocio y la capa web.

### Service

Las pruebas cubren principalmente:

- Creación de solicitudes.
- Detección de solicitudes activas duplicadas.
- Obtención de solicitudes existentes.
- Manejo de solicitudes inexistentes.
- Transición `RECIBIDA → EN_PROCESO`.
- Transiciones inválidas.
- Rechazo sin motivo.
- Rechazo con motivo.

### Controller

Las pruebas cubren principalmente:

- Validación de `ultimosCuatroDigitos`.
- Validación de `clienteId`.
- Creación de solicitudes.
- Obtención de solicitudes.
- Manejo de solicitudes inexistentes.
- Listado de solicitudes.
- Filtrado por estado.
- Cambio de estado.
- Manejo de transiciones inválidas.
- Respuestas de error estructuradas.

Para ejecutar la suite completa:

./gradlew clean test

## 12. Decisiones técnicas

### Separación de responsabilidades

Se utiliza una arquitectura por capas para mantener separadas las responsabilidades de presentación, lógica de negocio y persistencia.

### Estado controlado por el servidor

El estado inicial no es recibido desde el cliente. El Service establece automáticamente:

RECIBIDA

Esto evita que un consumidor cree directamente una solicitud en un estado no permitido.

### Máquina de estados

Las transiciones permitidas se centralizan en el enum `EstadoSolicitud`.

Esto permite mantener la regla de negocio en un único lugar y facilita su prueba y mantenimiento.

### DTOs

Los DTOs permiten separar el modelo de persistencia de los objetos expuestos por la API.

### Validaciones

Las validaciones de entrada se realizan mediante Jakarta Bean Validation.

### Excepciones centralizadas

Las excepciones de negocio y validación se manejan mediante `GlobalExceptionHandler`, evitando duplicar lógica de manejo de errores en los Controllers.

## 13. Supuestos

- `clienteId` se recibe como texto.
- Los últimos cuatro dígitos se almacenan únicamente como los cuatro caracteres finales de la tarjeta.
- No se almacena el número completo de la tarjeta.
- Las fechas de creación y actualización son generadas por el backend.
- El estado inicial siempre es `RECIBIDA`.
- H2 se utiliza como base de datos para facilitar la ejecución de la evaluación.
- No se implementa autenticación ni autorización, ya que no forman parte de los requerimientos obligatorios de la evaluación.
- No se implementan funcionalidades adicionales que no sean necesarias para los requerimientos principales.

## 14. Estado del proyecto

La aplicación cuenta con:

- API REST funcional.
- Persistencia mediante JPA/Hibernate.
- Base de datos H2.
- Validaciones de entrada.
- Reglas de negocio implementadas.
- Máquina de estados.
- Manejo centralizado de excepciones.
- Pruebas automatizadas.
- Arquitectura por capas.
- Gradle Wrapper para facilitar la ejecución.