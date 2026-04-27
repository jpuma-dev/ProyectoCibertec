# 🏪 ProyectoCibertec - Sistema de Gestión de Mercado
**Curso:** Desarrollo de Aplicaciones Web I (4694) — DAWI Cibertec  
**Stack:** Spring Boot 3.4.1 + SQL Server + Angular 19

---

## 📋 CORRECCIONES APLICADAS

| # | Problema original | Corrección |
|---|---|---|
| 1 | Spring Boot `4.0.5` (no existe) | Cambiado a `3.4.1` (versión estable) |
| 2 | Driver MySQL en lugar de SQL Server | Reemplazado por `mssql-jdbc` |
| 3 | Faltaba Spring Security y Login REST | Agregado `AuthController`, `AuthService`, `SecurityConfig` |
| 4 | Faltaba entidad `Usuario` con BCrypt | Agregada entidad `Usuario` que implementa `UserDetails` |
| 5 | Tests solo tenían `contextLoads()` vacío | Agregados 11 tests de insertar/actualizar/eliminar/listar |
| 6 | `application.properties` apuntaba a MySQL | Corregido para SQL Server Express |
| 7 | CORS configurado en clase aparte (redundante con Security) | Unificado en `SecurityConfig` |
| 8 | Anotaciones `@Data + @Getter + @Setter` redundantes | Limpiado a solo `@Data` en entidades |
| 9 | Angular no tenía pantalla de login | Agregado `LoginComponent` + `AuthGuard` |
| 10 | `ApiService` no enviaba credenciales | Agregado HTTP Basic Auth en cada petición |

---

## ⚙️ REQUISITOS PREVIOS

Instala lo siguiente antes de ejecutar:

- **Java 21** → https://adoptium.net/
- **Maven** → https://maven.apache.org/ (o usar `./mvnw`)
- **Node.js 18+** → https://nodejs.org/
- **Angular CLI** → `npm install -g @angular/cli`
- **SQL Server Express** → ya lo tienes instalado
- **Visual Studio Code** → con extensión "Extension Pack for Java"

---

## 🗄️ PASO 1 — Configurar la Base de Datos en SQL Server

Abre **SQL Server Management Studio** y ejecuta:

```sql
-- 1. Crear la base de datos
CREATE DATABASE bd_mercado;
GO

-- 2. Crear usuario para la aplicación (si usas 'sa', ya existe)
-- Si quieres usar Windows Auth, cambia application.properties

-- 3. Verificar que funciona
USE bd_mercado;
SELECT DB_NAME();
```

> **Nota:** Las tablas las crea Hibernate automáticamente con `ddl-auto=update`.

---

## ⚙️ PASO 2 — Configurar application.properties

Abre `src/main/resources/application.properties` y ajusta con tu nombre de servidor:

```properties
# Reemplaza DESKTOP-H3U62CV\SQLEXPRESS con el nombre de TU servidor
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=bd_mercado;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345678
```

Para saber tu nombre de servidor: abre SQL Server Management Studio → el nombre que aparece en "Server name".

---

## 🚀 PASO 3 — Ejecutar el Backend (Spring Boot)

### Opción A — Desde Visual Studio Code
1. Abre la carpeta `ProyectoCibertecMejorado` en VS Code
2. Instala la extensión **"Extension Pack for Java"**
3. Abre `ProyectoApplication.java`
4. Haz clic en el botón **▶ Run** que aparece encima del `main()`

### Opción B — Desde terminal
```bash
cd ProyectoCibertecMejorado
./mvnw spring-boot:run
```

El backend inicia en: **http://localhost:8080**

---

## 🧪 PASO 4 — Crear el primer usuario

El sistema necesita al menos un usuario en la BD para poder hacer login.  
Usa Postman o el navegador para llamar a:

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Esto guarda la contraseña **cifrada con BCrypt** en la tabla `usuarios`.

---

## 🌐 PASO 5 — Ejecutar el Frontend (Angular)

```bash
cd ProyectoCibertecMejorado/proyectoCibertecFronted

# Instalar dependencias (solo la primera vez)
npm install

# Iniciar el servidor de desarrollo
ng serve
```

El frontend inicia en: **http://localhost:4200**

Inicia sesión con `admin` / `admin123`.

---

## 🧪 PASO 6 — Ejecutar los Tests

```bash
cd ProyectoCibertecMejorado
./mvnw test
```

Los tests usan **H2 en memoria** (no necesitan SQL Server).  
Se prueban: insertar, listar, actualizar, eliminar para Socio, Puesto y Usuario.

---

## 📡 ENDPOINTS DE LA API

### Autenticación (públicos)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/auth/login` | Login con usuario y password |
| POST | `/api/auth/register` | Registrar nuevo usuario (password con BCrypt) |

### Socios (requieren autenticación)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/socios` | Listar todos |
| POST | `/api/socios` | Crear socio |
| PUT | `/api/socios/{dni}` | Actualizar por DNI |
| DELETE | `/api/socios/{dni}` | Eliminar por DNI |

### Puestos
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/puestos` | Listar todos |
| POST | `/api/puestos` | Crear puesto |
| PUT | `/api/puestos/{id}` | Actualizar |
| DELETE | `/api/puestos/{id}` | Eliminar |

### Deudas
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/deudas` | Listar todas |
| POST | `/api/deudas` | Crear deuda |
| POST | `/api/deudas/generar-masiva` | Generar deudas masivas |

### Pagos
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/pagos` | Registrar pago |
| GET | `/api/pagos/flujo-caja-diario` | Flujo de caja del día |

### Reportes
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/reportes/deudas/socio` | Deudas pendientes por socio |
| GET | `/api/reportes/deudas/socio/export/excel` | Exportar a Excel |
| GET | `/api/reportes/flujo-caja` | Flujo de caja por rango |
| GET | `/api/reportes/morosidad` | Reporte de morosidad |

---

## 📤 SUBIR A GITHUB

### Primera vez
```bash
# 1. Inicializar repositorio (si no tiene .git)
cd ProyectoCibertecMejorado
git init

# 2. Agregar todos los archivos
git add .

# 3. Primer commit
git commit -m "feat: proyecto inicial con Spring Boot + SQL Server + Angular + Spring Security"

# 4. Crear repositorio en GitHub.com (sin README)
#    Ve a github.com → New repository → crea uno vacío

# 5. Conectar y subir
git remote add origin https://github.com/TU_USUARIO/ProyectoCibertec.git
git branch -M main
git push -u origin main
```

### Commits siguientes
```bash
git add .
git commit -m "feat: descripción del cambio"
git push
```

### Archivos que NO se suben (.gitignore ya los excluye)
- `target/` (compilados Java)
- `node_modules/` (dependencias Angular)
- `.angular/` (caché Angular)
- `*.class` (bytecode)

---

## 🏗️ ARQUITECTURA DEL PROYECTO

```
ProyectoCibertecMejorado/
│
├── src/main/java/com/cibertec/proyecto/
│   ├── ProyectoApplication.java        ← Punto de entrada
│   ├── config/
│   │   └── SecurityConfig.java         ← Spring Security + BCrypt + CORS
│   ├── controllers/
│   │   ├── AuthController.java         ← POST /api/auth/login  (NUEVO)
│   │   ├── SocioController.java        ← GET/POST/PUT/DELETE /api/socios
│   │   ├── PuestoController.java       ← GET/POST/PUT/DELETE /api/puestos
│   │   ├── DeudaController.java        ← GET/POST /api/deudas
│   │   ├── PagoController.java         ← POST /api/pagos
│   │   ├── ReporteController.java      ← GET /api/reportes/*
│   │   └── DashboardController.java    ← GET /api/dashboard/stats
│   ├── entities/
│   │   ├── Usuario.java                ← Entidad nueva con UserDetails + BCrypt
│   │   ├── Socio.java
│   │   ├── Puesto.java
│   │   ├── ConceptoDeuda.java
│   │   ├── Deuda.java
│   │   └── Pago.java
│   ├── repositories/                   ← Spring Data JPA (CRUD automático)
│   ├── services/
│   │   ├── AuthService.java            ← Login + Register con BCrypt (NUEVO)
│   │   ├── UsuarioDetailsService.java  ← UserDetailsService para Spring Security
│   │   └── ...
│   └── exceptions/
│       └── GlobalExceptionHandler.java ← Manejo centralizado de errores
│
├── src/test/
│   └── ProyectoApplicationTests.java   ← 11 tests CRUD con H2
│
├── proyectoCibertecFronted/            ← Frontend Angular 19
│   └── src/app/
│       ├── login.component.ts          ← Pantalla de login (NUEVO)
│       ├── auth.guard.ts               ← Protección de rutas (NUEVO)
│       ├── app.routes.ts               ← Rutas con guard
│       ├── services/api.service.ts     ← HTTP con autenticación Basic
│       ├── socios.component.ts
│       ├── puestos.component.ts
│       ├── deudas.component.ts
│       ├── cobranza.component.ts
│       └── reportes.component.ts
│
└── pom.xml                             ← Spring Boot 3.4.1 + SQL Server + Security
```
