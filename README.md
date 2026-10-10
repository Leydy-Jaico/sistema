# Gestión de Clientes - Laboratorio 04 IS388

Proyecto desarrollado para la práctica de **Gestión Ágil Avanzada (Scrum)** del curso **IS388 - Construcción y Evolución de Software**.

El sistema implementa una API REST para la gestión de clientes, permitiendo registrar, consultar, actualizar, buscar y eliminar información. Además, incorpora validaciones, manejo global de errores, documentación mediante Swagger/OpenAPI y pruebas automatizadas.

---

## 1. Objetivo del proyecto

Desarrollar una aplicación para la gestión de clientes aplicando buenas prácticas de construcción de software, arquitectura por capas, principios de trabajo ágil y control de versiones.

El sistema permite:

- Registrar clientes.
- Listar clientes.
- Consultar clientes por ID.
- Buscar clientes por DNI.
- Buscar clientes por nombre.
- Actualizar información.
- Eliminar clientes.
- Validar datos de entrada.
- Detectar DNI duplicados.
- Gestionar errores mediante respuestas HTTP adecuadas.
- Documentar y probar la API mediante Swagger.

---

## 2. Arquitectura técnica

La solución utiliza una arquitectura cliente-servidor.

```text
Frontend
   ↓
API REST - Spring Boot
   ↓
Spring Data JPA / Hibernate
   ↓
SQL Server
```

### Backend

- Spring Boot 3.3.5
- Java 21
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Maven
- JUnit 5
- Mockito
- Swagger / OpenAPI

### Base de datos

- Microsoft SQL Server
- Base de datos: `gestion_clientes`
- Puerto: `1433`

### Frontend

- HTML5
- CSS3
- JavaScript
- Bootstrap
- Live Server

---

## 3. Estructura del proyecto

```text
sistema/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── clientes/
│   │   │   │           └── backend/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
├── base_de_datos/
│   └── clientes.sql
│
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── script.js
│
├── .github/
│   └── workflows/
│
└── README.md
```

---

## 4. Historias de usuario

### HU-01 - Gestión CRUD de clientes

**Como** usuario del sistema  
**Quiero** registrar, consultar, actualizar y eliminar clientes  
**Para** mantener actualizada la información de los clientes.

### Criterios de aceptación

#### Escenario 1 - Registro exitoso

**Given** que el usuario envía una solicitud `POST /api/v1/clientes` con datos válidos y un DNI único.  
**When** la API procesa la solicitud.  
**Then** debe devolver `201 Created` y los datos del cliente registrado.

#### Escenario 2 - DNI duplicado

**Given** que existe un cliente registrado con determinado DNI.  
**When** se intenta registrar otro cliente con el mismo DNI.  
**Then** la API debe devolver `409 Conflict`.

#### Escenario 3 - Consulta por ID

**Given** que existe un cliente registrado.  
**When** se consulta mediante `GET /api/v1/clientes/{id}`.  
**Then** debe devolver `200 OK` con la información correspondiente.

#### Escenario 4 - Cliente inexistente

**Given** un identificador que no corresponde a ningún cliente.  
**When** se realiza una consulta por ID.  
**Then** la API debe devolver `404 Not Found`.

#### Escenario 5 - Actualización exitosa

**Given** que existe un cliente registrado.  
**When** se envía una solicitud `PUT /api/v1/clientes/{id}` con datos válidos.  
**Then** debe devolver `200 OK` con la información actualizada.

#### Escenario 6 - Eliminación exitosa

**Given** que existe un cliente registrado.  
**When** se ejecuta `DELETE /api/v1/clientes/{id}`.  
**Then** debe devolver `204 No Content`.

---

### HU-02 - Búsqueda de clientes

**Como** usuario del sistema  
**Quiero** buscar clientes mediante DNI o nombre  
**Para** localizar rápidamente su información.

### Criterios de aceptación

#### Búsqueda por DNI

**Given** una petición:

```text
GET /api/v1/clientes?dni=12345678
```

**When** la API consulta la información almacenada.  
**Then** debe devolver `200 OK` con el cliente correspondiente.

#### Búsqueda por nombre

**Given** una petición:

```text
GET /api/v1/clientes?nombre=Carlos
```

**When** la API realiza la búsqueda.  
**Then** debe devolver `200 OK` con las coincidencias encontradas.

La búsqueda por nombre permite coincidencias parciales y no distingue entre mayúsculas y minúsculas.

---

## 5. Endpoints de la API

| Método | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/v1/clientes` | Registrar cliente | `201 Created` |
| GET | `/api/v1/clientes` | Listar clientes | `200 OK` |
| GET | `/api/v1/clientes/{id}` | Obtener cliente por ID | `200 OK` / `404 Not Found` |
| GET | `/api/v1/clientes?dni=...` | Buscar por DNI | `200 OK` |
| GET | `/api/v1/clientes?nombre=...` | Buscar por nombre | `200 OK` |
| PUT | `/api/v1/clientes/{id}` | Actualizar cliente | `200 OK` / `404 Not Found` |
| DELETE | `/api/v1/clientes/{id}` | Eliminar cliente | `204 No Content` / `404 Not Found` |

---

## 6. Modelo de Cliente

El cliente contiene los siguientes datos:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador único |
| `dni` | String | DNI de 8 dígitos |
| `nombre` | String | Nombre del cliente |
| `apellido` | String | Apellido del cliente |
| `email` | String | Correo electrónico |
| `telefono` | String | Número telefónico |

Ejemplo:

```json
{
  "id": 1,
  "dni": "12345678",
  "nombre": "Carlos",
  "apellido": "Quispe",
  "email": "carlos@gmail.com",
  "telefono": "987654321"
}
```

---

## 7. Validaciones implementadas

### DNI

- Obligatorio.
- Debe contener exactamente 8 dígitos.
- Debe ser único en el sistema.

### Nombre

- Obligatorio.
- Longitud máxima controlada.

### Apellido

- Obligatorio.
- Longitud máxima controlada.

### Correo electrónico

- Obligatorio.
- Debe tener un formato válido.

### Teléfono

- Debe contener entre 9 y 15 dígitos.

---

## 8. Manejo global de errores

La aplicación implementa manejo centralizado de excepciones mediante:

```java
@RestControllerAdvice
```

Asimismo, utiliza `ProblemDetail` para retornar respuestas estructuradas.

Ejemplo de respuesta cuando se intenta registrar un DNI duplicado:

```json
{
  "type": "about:blank",
  "title": "Conflicto",
  "status": 409,
  "detail": "Ya existe un cliente con ese DNI",
  "instance": "/api/v1/clientes"
}
```

Principales códigos HTTP utilizados:

| Código | Significado |
|---|---|
| `200 OK` | Operación realizada correctamente |
| `201 Created` | Cliente creado correctamente |
| `204 No Content` | Cliente eliminado correctamente |
| `400 Bad Request` | Datos enviados inválidos |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | DNI duplicado |
| `500 Internal Server Error` | Error interno del servidor |

---

## 9. Definition of Ready - DoR

Una historia se considera preparada para ingresar al Sprint cuando cumple:

- [x] La historia está redactada de forma clara.
- [x] La historia cumple criterios INVEST.
- [x] Se identifica el valor esperado para el usuario.
- [x] Los criterios de aceptación están definidos.
- [x] El contrato de la API está identificado.
- [x] Se conocen los endpoints involucrados.
- [x] Se conocen los códigos HTTP esperados.
- [x] Las reglas de validación están definidas.
- [x] Las dependencias técnicas están identificadas.
- [x] La historia puede ser estimada.
- [x] Se utiliza una escala de Story Points.

---

## 10. Estimación con Story Points

Se utiliza la escala Fibonacci:

```text
1, 2, 3, 5, 8, 13
```

| Historia de usuario | Estimación |
|---|---:|
| HU-01 - Gestión CRUD de clientes | 8 SP |
| HU-02 - Búsqueda por DNI y nombre | 5 SP |

### Justificación

La **HU-01** posee mayor esfuerzo porque comprende creación, consulta, actualización, eliminación, validaciones y manejo de errores.

La **HU-02** posee menor complejidad porque se concentra principalmente en consultas y filtrado de información.

### Story Points totales

```text
13 Story Points
```

---

## 11. Definition of Done - DoD

Una funcionalidad se considera terminada cuando cumple:

- [x] Código implementado.
- [x] Aplicación compilando correctamente.
- [x] API REST funcional.
- [x] Persistencia en base de datos.
- [x] Validaciones implementadas.
- [x] Manejo global de excepciones.
- [x] Endpoints documentados mediante Swagger/OpenAPI.
- [x] Pruebas unitarias implementadas.
- [x] Pruebas ejecutadas correctamente.
- [x] Resultado `BUILD SUCCESS` mediante Maven.
- [x] CRUD probado mediante Swagger.
- [x] Respuestas HTTP verificadas.
- [x] Integración del frontend con el backend.
- [ ] Pull Request revisado y aprobado.
- [ ] Análisis estático mediante SonarQube, Codacy u otra herramienta.

---

## 12. Pruebas funcionales realizadas

La API fue validada utilizando Swagger UI.

### Prueba 1 - Crear cliente

Endpoint:

```text
POST /api/v1/clientes
```

Ejemplo:

```json
{
  "dni": "87654321",
  "nombre": "Ana",
  "apellido": "Huamán",
  "email": "ana@gmail.com",
  "telefono": "987654322"
}
```

Resultado:

```text
201 Created
```

Se comprobó la creación correcta del cliente.

---

### Prueba 2 - DNI duplicado

Se intentó registrar otro cliente utilizando un DNI ya existente.

Resultado:

```text
409 Conflict
```

Respuesta:

```json
{
  "type": "about:blank",
  "title": "Conflicto",
  "status": 409,
  "detail": "Ya existe un cliente con ese DNI",
  "instance": "/api/v1/clientes"
}
```

---

### Prueba 3 - Listar clientes

Endpoint:

```text
GET /api/v1/clientes
```

Resultado:

```text
200 OK
```

---

### Prueba 4 - Obtener cliente por ID

Endpoint:

```text
GET /api/v1/clientes/{id}
```

Resultado para un cliente existente:

```text
200 OK
```

Resultado para un cliente inexistente:

```text
404 Not Found
```

---

### Prueba 5 - Buscar por DNI

Endpoint:

```text
GET /api/v1/clientes?dni=12345678
```

Resultado:

```text
200 OK
```

---

### Prueba 6 - Buscar por nombre

Endpoint:

```text
GET /api/v1/clientes?nombre=Carlos
```

Resultado:

```text
200 OK
```

---

### Prueba 7 - Actualizar cliente

Endpoint:

```text
PUT /api/v1/clientes/{id}
```

Ejemplo:

```json
{
  "dni": "87654321",
  "nombre": "Ana",
  "apellido": "Huamán",
  "email": "ana.actualizada@gmail.com",
  "telefono": "999888777"
}
```

Resultado:

```text
200 OK
```

---

### Prueba 8 - Eliminar cliente

Endpoint:

```text
DELETE /api/v1/clientes/{id}
```

Resultado:

```text
204 No Content
```

---

## 13. Pruebas automatizadas

Las pruebas automatizadas fueron ejecutadas mediante Maven.

Comando:

```bash
mvn test
```

Resultado obtenido:

```text
BUILD SUCCESS
```

Esto confirma que las pruebas automatizadas configuradas en el proyecto se ejecutaron correctamente.

Las tecnologías utilizadas para pruebas son:

- JUnit 5
- Mockito
- Spring Boot Test

---

## 14. Swagger / OpenAPI

La API está documentada mediante Swagger/OpenAPI.

Mientras el backend se encuentra ejecutándose, Swagger está disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permite:

- Visualizar todos los endpoints.
- Consultar los parámetros requeridos.
- Enviar solicitudes HTTP.
- Probar el CRUD.
- Visualizar códigos HTTP.
- Revisar cuerpos JSON.
- Comprobar validaciones.
- Comprobar respuestas de error.

---

## 15. Configuración de SQL Server

La aplicación utiliza Microsoft SQL Server.

Configuración utilizada:

```text
Base de datos: gestion_clientes
Puerto: 1433
Usuario: usuario_clientes
```

La configuración se encuentra en:

```text
backend/src/main/resources/application.properties
```

Ejemplo:

```properties
spring.application.name=backend

server.port=8080

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=gestion_clientes;encrypt=true;trustServerCertificate=true
spring.datasource.username=usuario_clientes
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

> Se recomienda no publicar contraseñas reales en repositorios públicos.

---

## 16. Preparación de la base de datos

El script de base de datos se encuentra en:

```text
base_de_datos/clientes.sql
```

Debe ejecutarse mediante SQL Server Management Studio.

La tabla principal utilizada es:

```text
clientes
```

Campos principales:

```text
id
dni
nombre
apellido
email
telefono
```

---

## 17. Ejecución del backend

### Requisitos

Antes de ejecutar el backend se requiere:

- Java 21.
- Maven.
- SQL Server.
- Puerto `1433` habilitado.
- Base de datos `gestion_clientes`.
- Credenciales SQL Server configuradas.

### Verificar Java

```bash
java -version
```

### Verificar Maven

```bash
mvn -version
```

### Ejecutar backend

Ubicarse dentro de:

```text
backend/
```

Ejecutar:

```bash
mvn spring-boot:run
```

Cuando el servidor inicia correctamente se muestra:

```text
Tomcat started on port 8080
Started BackendApplication
```

La API queda disponible en:

```text
http://localhost:8080/api/v1/clientes
```

---

## 18. Ejecución del frontend

Abrir el archivo:

```text
frontend/index.html
```

mediante **Live Server** en Visual Studio Code.

El frontend consume:

```text
http://localhost:8080/api/v1/clientes
```

Por ello, el backend debe estar ejecutándose antes de utilizar el frontend.

Flujo de funcionamiento:

```text
Usuario
   ↓
Frontend
   ↓
Fetch / HTTP
   ↓
API REST Spring Boot
   ↓
JPA / Hibernate
   ↓
SQL Server
```

---

## 19. Flujo Git

### Inicializar repositorio

```bash
git init
```

Agregar archivos:

```bash
git add .
```

Realizar commit inicial:

```bash
git commit -m "chore: estructura inicial del proyecto"
```

### Crear rama develop

```bash
git checkout -b develop
```

### Crear rama para CRUD

```bash
git checkout -b feature/crud-clientes
```

### Registrar cambios

```bash
git add .
git commit -m "feat: implementar CRUD de clientes"
```

### Subir rama

```bash
git push origin feature/crud-clientes
```

Posteriormente se puede crear un Pull Request hacia la rama `develop`.

---

## 20. Convención de commits

Ejemplos:

```text
feat: implementar registro de clientes
feat: agregar búsqueda por DNI
feat: implementar actualización de clientes
feat: implementar eliminación de clientes
fix: corregir validación de DNI duplicado
test: agregar pruebas de ClienteService
docs: actualizar README
chore: configurar proyecto Spring Boot
```

---

## 21. Gestión Scrum

### Product Owner

Responsable de:

- Priorizar las historias de usuario.
- Definir necesidades del producto.
- Establecer criterios de aceptación.
- Validar el valor entregado.

### Scrum Master

Responsable de:

- Facilitar la aplicación de Scrum.
- Apoyar en la eliminación de impedimentos.
- Promover buenas prácticas ágiles.
- Facilitar la mejora continua.

### Developers

Responsables de:

- Diseñar la solución.
- Implementar funcionalidades.
- Realizar pruebas.
- Corregir errores.
- Documentar el sistema.
- Mantener la calidad técnica.

---

## 22. Sprint Backlog

| Tarea | Historia relacionada | Estado |
|---|---|---|
| Crear entidad Cliente | HU-01 | Completado |
| Crear repositorio de clientes | HU-01 | Completado |
| Implementar servicio | HU-01 | Completado |
| Implementar controlador REST | HU-01 | Completado |
| Implementar registro de cliente | HU-01 | Completado |
| Implementar consulta por ID | HU-01 | Completado |
| Implementar actualización | HU-01 | Completado |
| Implementar eliminación | HU-01 | Completado |
| Implementar validaciones | HU-01 | Completado |
| Implementar manejo de excepciones | HU-01 | Completado |
| Implementar búsqueda por DNI | HU-02 | Completado |
| Implementar búsqueda por nombre | HU-02 | Completado |
| Crear pruebas automatizadas | HU-01 / HU-02 | Completado |
| Documentar API con Swagger | HU-01 / HU-02 | Completado |
| Integrar frontend con backend | HU-01 / HU-02 | Completado |
| Documentar proyecto | HU-01 / HU-02 | Completado |

---

## 23. Flujo de desarrollo propuesto

```text
Product Backlog
      ↓
Refinamiento
      ↓
Definition of Ready
      ↓
Sprint Planning
      ↓
Sprint Backlog
      ↓
Desarrollo
      ↓
Pruebas
      ↓
Code Review
      ↓
Definition of Done
      ↓
Incremento
```

---

## 24. Estrategia de ramas

Se propone utilizar el siguiente flujo:

```text
main
 │
 └── develop
      │
      ├── feature/crud-clientes
      ├── feature/busqueda-clientes
      └── fix/validacion-dni
```

Las funcionalidades deben desarrollarse en ramas independientes antes de integrarse.

---

## 25. Pull Requests

Para mantener la calidad del proyecto se recomienda:

- Crear Pull Requests pequeños.
- Evitar cambios demasiado extensos.
- Revisar el código antes del merge.
- Verificar el cumplimiento del DoD.
- Ejecutar pruebas antes de aprobar.
- Mantener commits descriptivos.

---

## 26. Métricas ágiles

### Velocity

Cantidad de Story Points terminados dentro del Sprint.

```text
Velocity = 13 Story Points
```

### Lead Time

Tiempo transcurrido desde que una necesidad es solicitada hasta que se entrega.

### Cycle Time

Tiempo transcurrido desde que una tarea comienza a desarrollarse hasta que se finaliza.

### PR Review Lead Time

Tiempo transcurrido desde que se crea un Pull Request hasta que se revisa y aprueba.

---

## 27. Integración continua

El proyecto contiene la estructura:

```text
.github/workflows/
```

para permitir la configuración de pipelines de integración continua.

Un pipeline puede ejecutar automáticamente:

```bash
mvn test
```

con el objetivo de validar que las pruebas continúen funcionando antes de integrar nuevos cambios.

---

## 28. Calidad de código

Actualmente el proyecto incorpora:

- Arquitectura por capas.
- Validaciones.
- Manejo centralizado de errores.
- Pruebas automatizadas.
- Documentación Swagger.
- Convención de commits.
- Separación de responsabilidades.

Como mejora futura pueden incorporarse:

- SonarQube.
- Codacy.
- JaCoCo.
- Métricas de cobertura.
- Reglas adicionales de calidad.
- Análisis automático de vulnerabilidades.

---

## 29. Seguridad

Se recomienda:

- No almacenar contraseñas reales directamente en GitHub.
- Utilizar variables de entorno.
- Mantener credenciales fuera del repositorio.
- Configurar diferentes credenciales para desarrollo y producción.

Ejemplo futuro:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## 30. Retrospectiva

### ¿Qué salió bien?

- Se implementó una API REST funcional.
- Se logró configurar Java 21 y Maven.
- Se estableció correctamente la conexión entre Spring Boot y SQL Server.
- Se habilitó SQL Server mediante TCP/IP en el puerto 1433.
- Se configuró autenticación SQL Server.
- Se organizó el backend mediante una arquitectura por capas.
- Se implementaron validaciones.
- Se controló correctamente el DNI duplicado.
- Se implementó manejo global de excepciones.
- Swagger permitió comprobar los endpoints.
- Las pruebas automatizadas finalizaron con `BUILD SUCCESS`.
- Se logró integrar frontend, backend y base de datos.

### ¿Qué dificultades se presentaron?

- Configuración inicial de Java.
- Configuración de Maven.
- Configuración del puerto 1433.
- Configuración de autenticación de SQL Server.
- Conexión entre Spring Boot y SQL Server.
- Validación del usuario de base de datos.

### ¿Cómo se solucionaron?

- Instalación y configuración de Java 21.
- Instalación y configuración de Maven.
- Habilitación de TCP/IP.
- Configuración del puerto 1433.
- Activación de autenticación mixta.
- Verificación de usuario y contraseña SQL.
- Pruebas progresivas mediante Swagger.

---

## 31. Resultados obtenidos

El sistema permite completar correctamente las siguientes operaciones:

```text
POST    /api/v1/clientes
GET     /api/v1/clientes
GET     /api/v1/clientes/{id}
GET     /api/v1/clientes?dni=...
GET     /api/v1/clientes?nombre=...
PUT     /api/v1/clientes/{id}
DELETE  /api/v1/clientes/{id}
```

Asimismo, se verificaron respuestas HTTP como:

```text
200 OK
201 Created
204 No Content
404 Not Found
409 Conflict
```

Las pruebas automatizadas finalizaron con:

```text
BUILD SUCCESS
```

---

## 32. Conclusiones

El desarrollo permitió implementar una solución funcional para la gestión de clientes utilizando Spring Boot, Java 21 y SQL Server.

La arquitectura por capas permitió separar adecuadamente las responsabilidades entre controlador, servicio, repositorio y persistencia, mejorando la organización del código.

La integración de Spring Data JPA e Hibernate facilitó el acceso y manipulación de los datos almacenados en SQL Server.

Las validaciones implementadas permitieron garantizar la consistencia de la información ingresada y controlar situaciones como el registro de DNI duplicados.

El manejo global de excepciones permitió retornar respuestas HTTP adecuadas ante diferentes situaciones, facilitando la comunicación entre frontend y backend.

Swagger/OpenAPI permitió documentar y probar de forma interactiva todos los endpoints disponibles.

Las pruebas automatizadas ejecutadas mediante Maven finalizaron correctamente con `BUILD SUCCESS`, verificando el correcto comportamiento de los componentes evaluados.

Finalmente, la aplicación de Scrum, Definition of Ready, Definition of Done, Sprint Backlog y Story Points permitió relacionar el desarrollo técnico con prácticas ágiles de construcción de software.

---

## 33. Recomendaciones

- Mantener las credenciales fuera del repositorio.
- Utilizar variables de entorno para información sensible.
- Incrementar progresivamente la cobertura de pruebas.
- Incorporar herramientas como SonarQube o Codacy.
- Configurar análisis de cobertura mediante JaCoCo.
- Implementar Pull Requests pequeños y revisables.
- Mantener actualizada la documentación Swagger.
- Utilizar ramas independientes para nuevas funcionalidades.
- Mantener commits pequeños y descriptivos.
- Implementar más pruebas de integración.
- Mejorar progresivamente la interfaz gráfica del frontend.
- Configurar un pipeline de integración continua.

---

## 34. Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje backend |
| Spring Boot 3.3.5 | Framework principal |
| Spring Web | Desarrollo de API REST |
| Spring Data JPA | Persistencia |
| Hibernate | ORM |
| Jakarta Validation | Validación |
| SQL Server | Base de datos |
| Maven | Gestión de dependencias |
| Swagger / OpenAPI | Documentación de API |
| JUnit 5 | Pruebas |
| Mockito | Mocking |
| HTML5 | Frontend |
| CSS3 | Estilos |
| JavaScript | Lógica frontend |
| Bootstrap | Diseño de interfaz |
| Git | Control de versiones |
| GitHub | Repositorio remoto |

---

## 35. Autor

**Leydy Yesenia Jaico Quispe**

Curso:

**IS388 - Construcción y Evolución de Software**

Laboratorio:

**Laboratorio 04 - Gestión Ágil Avanzada (Scrum)**

---

## 36. Estado del proyecto

```text
Backend:      Funcional
Frontend:     Funcional
SQL Server:   Conectado
Swagger:      Funcional
CRUD:         Funcional
Validaciones: Funcionales
Pruebas:      BUILD SUCCESS
```

**Estado general: funcional y listo para documentación y entrega académica.**