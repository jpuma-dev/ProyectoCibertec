# Backend Proyecto Cibertec - Mercado

Backend Spring Boot para administrar socios, puestos, conceptos de deuda, deudas,
pagos y reportes del sistema de mercado.

## Requisitos

- Java JDK 21.
- Maven Wrapper incluido en el proyecto (`mvnw.cmd`).
- MySQL ejecutandose localmente.
- Base de datos `bd_mercado`.

## Configuracion de base de datos

La configuracion se encuentra en:

```text
src/main/resources/application.properties
```

Valores actuales:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/bd_mercado
spring.datasource.username=sa
spring.datasource.password=12345678
spring.jpa.hibernate.ddl-auto=update
```

Antes de ejecutar, crea la base de datos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS bd_mercado;
```

Si tu usuario o clave de MySQL son distintos, modifica
`spring.datasource.username` y `spring.datasource.password`.

## Ejecutar el backend

Desde esta carpeta `backend`, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio queda disponible en:

```text
http://localhost:8080
```

La pagina inicial estatica se puede abrir en:

```text
http://localhost:8080/
```

## Verificar que esta funcionando

En otra terminal PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/dashboard/stats
```

Tambien puedes revisar el resumen de deudas:

```powershell
Invoke-RestMethod http://localhost:8080/api/deudas/resumen
```

Si responde con JSON, el backend esta conectado y atendiendo peticiones.

## Ejecutar pruebas del proyecto

Para compilar y ejecutar las pruebas automaticas:

```powershell
.\mvnw.cmd test
```

Para limpiar y empaquetar el proyecto:

```powershell
.\mvnw.cmd clean package
```

El archivo generado queda en:

```text
target/
```

## Endpoints principales

- `GET /api/dashboard/stats`: resumen general del sistema.
- `GET /api/socios`: lista completa de socios.
- `GET /api/socios/paginado`: lista paginada de socios.
- `GET /api/puestos`: lista de puestos.
- `GET /api/conceptos-deuda`: conceptos de deuda.
- `GET /api/deudas`: deudas registradas.
- `GET /api/deudas/resumen`: resumen financiero de deudas.
- `GET /api/pagos`: pagos registrados.
- `GET /api/reportes/morosidad`: reporte de morosidad.
- `GET /api/reportes/deudas/export/excel`: exportacion Excel de deudas.

Para pruebas manuales mas detalladas, revisa:

```text
docs/PRUEBAS.md
```
