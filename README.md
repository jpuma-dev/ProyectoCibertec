# Sistema Web de Gestión de Pagos y Cobranza para un Mercado

Sistema web desarrollado para administrar la cobranza diaria de un mercado,
centralizando el registro de socios, puestos, conceptos de deuda, deudas,
pagos y reportes operativos.

El proyecto esta dividido en dos aplicaciones:

- `backend`: API REST desarrollada con Spring Boot.
- `frontend`: interfaz web desarrollada con Angular.

## Tecnologias utilizadas

### Backend

- Java JDK 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Maven Wrapper
- Apache POI para exportacion de reportes Excel

### Frontend

- Angular 19
- TypeScript
- RxJS
- Angular Forms
- Angular Router

## Requisitos previos

Antes de ejecutar el proyecto, instala y verifica:

- Java JDK 21 o superior.
- Node.js y npm.
- MySQL Server.
- Git.
- Un editor o IDE, por ejemplo IntelliJ IDEA o Visual Studio Code.

Verifica las versiones con:

```powershell
java -version
node -v
npm -v
git --version
```

## Configuracion de la base de datos

El backend usa MySQL y espera una base de datos llamada `bd_mercado`.

Ejecuta en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS bd_mercado;
```

La configuracion de conexion esta en:

```text
backend/src/main/resources/application.properties
```

Valores actuales:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/bd_mercado
spring.datasource.username=sa
spring.datasource.password=12345678
spring.jpa.hibernate.ddl-auto=update
```

Si tu MySQL usa otro usuario o contrasena, cambia:

```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CLAVE
```

## Ejecucion del backend

Abre una terminal en la carpeta `backend`:

```powershell
cd backend
```

Ejecuta la API:

```powershell
.\mvnw.cmd spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

Verifica que responda:

```powershell
Invoke-RestMethod http://localhost:8080/api/dashboard/stats
```

Si devuelve una respuesta JSON, la API esta ejecutandose correctamente.

## Ejecucion del frontend

Abre otra terminal en la carpeta `frontend`:

```powershell
cd frontend
```

Instala las dependencias:

```powershell
npm install
```

Ejecuta Angular:

```powershell
npm start
```

El frontend queda disponible en:

```text
http://localhost:4200
```

El frontend consume la API desde:

```text
http://localhost:8080/api
```

Por eso el backend debe estar activo antes de probar la interfaz web.

## Pruebas y validacion

### Backend

Desde la carpeta `backend`, ejecuta:

```powershell
.\mvnw.cmd test
```

Para compilar y generar el paquete:

```powershell
.\mvnw.cmd clean package
```

### Frontend

Desde la carpeta `frontend`, ejecuta:

```powershell
npm run build
```

Este comando valida que la aplicacion Angular compile correctamente.

## Endpoints principales

- `GET /api/dashboard/stats`: estadisticas generales del sistema.
- `GET /api/socios`: listado de socios.
- `GET /api/socios/paginado`: listado paginado de socios.
- `POST /api/socios`: registro de socios.
- `GET /api/puestos`: listado de puestos.
- `POST /api/puestos`: registro de puestos.
- `GET /api/conceptos-deuda`: conceptos de deuda.
- `POST /api/deudas`: registro de deuda individual.
- `POST /api/deudas/generar-masiva`: generacion masiva de deudas.
- `POST /api/pagos`: registro de pagos.
- `GET /api/reportes/flujo-caja-diario`: reporte de caja diaria.
- `GET /api/reportes/morosidad`: reporte de morosidad.
- `GET /api/reportes/deudas/export/excel`: exportacion de deudas a Excel.

## Estructura del proyecto

```text
ProyectoCibertec/
+-- backend/
|   +-- src/main/java/com/cibertec/proyecto/
|   +-- src/main/resources/
|   +-- pom.xml
|   +-- mvnw.cmd
+-- frontend/
|   +-- src/
|   +-- angular.json
|   +-- package.json
|   +-- package-lock.json
+-- .gitignore
+-- README.md
```

## Orden recomendado para ejecutar

1. Crear la base de datos `bd_mercado` en MySQL.
2. Configurar usuario y clave en `backend/src/main/resources/application.properties`.
3. Iniciar el backend en `http://localhost:8080`.
4. Instalar dependencias del frontend con `npm install`.
5. Iniciar el frontend en `http://localhost:4200`.
6. Probar el flujo desde la interfaz web.

## Notas de despliegue local

- El backend esta configurado para ejecutarse en el puerto `8080`.
- El frontend esta configurado para ejecutarse en el puerto `4200`.
- La configuracion CORS del backend permite peticiones desde `http://localhost:4200`.
- Las tablas se actualizan automaticamente por Hibernate usando `spring.jpa.hibernate.ddl-auto=update`.
