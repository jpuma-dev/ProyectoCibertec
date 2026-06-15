# Guia de pruebas manuales

Esta guia permite probar el backend desde PowerShell o Postman despues de
levantar el proyecto en `http://localhost:8080`.

## 1. Confirmar estado del backend

```powershell
Invoke-RestMethod http://localhost:8080/api/dashboard/stats
```

Resultado esperado: respuesta JSON con totales de socios, puestos, deudas,
conceptos y recaudacion del dia.

## 2. Crear un socio

```powershell
$body = @{
  nombre = "Juan"
  apellido = "Perez"
  dni = "12345678"
  telefono = "987654321"
  email = "juan.perez@test.com"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/socios `
  -ContentType "application/json" `
  -Body $body
```

Resultado esperado: socio creado con datos normalizados y un identificador.

## 3. Listar socios paginados

```powershell
Invoke-RestMethod "http://localhost:8080/api/socios/paginado?page=0&size=10"
```

Usa este endpoint para pruebas con varios registros, porque evita traer toda la
tabla de socios en una sola respuesta.

## 4. Crear un puesto

Reemplaza `socioId` por el identificador devuelto al crear el socio.

```powershell
$body = @{
  numero = "A01"
  descripcion = "Puesto principal"
  socioId = 1
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/puestos `
  -ContentType "application/json" `
  -Body $body
```

Resultado esperado: puesto creado y asignado al socio.

## 5. Revisar conceptos de deuda

```powershell
Invoke-RestMethod http://localhost:8080/api/conceptos-deuda
```

El sistema crea conceptos iniciales al arrancar si no existen.

## 6. Crear una deuda

Reemplaza `conceptoId` y `puestoIds` segun los datos existentes.

```powershell
$body = @{
  monto = 50.00
  fecha = "2026-06-30"
  conceptoId = 1
  puestoIds = @(1)
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/deudas `
  -ContentType "application/json" `
  -Body $body
```

Resultado esperado: deuda pendiente creada para el puesto seleccionado.

## 7. Generar deudas masivas

Este endpoint crea deudas para los puestos seleccionados. Si no se envia
`puestoIds`, usa todos los puestos con socio asignado.

```powershell
$body = @{
  monto = 25.00
  fecha = "2026-06-30"
  conceptoId = 1
  puestoIds = @(1)
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/deudas/generar-masiva `
  -ContentType "application/json" `
  -Body $body
```

Resultado esperado: respuesta con la cantidad de deudas generadas.

## 8. Registrar un pago

Reemplaza `deudaId` y `monto` con los datos de una deuda pendiente. El monto debe
ser el total exacto de la deuda.

```powershell
$body = @{
  deudaId = 1
  monto = 50.00
  metodoPago = "EFECTIVO"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/pagos `
  -ContentType "application/json" `
  -Body $body
```

Resultado esperado: pago registrado y deuda marcada como `PAGADO`.

## 9. Probar reportes

```powershell
Invoke-RestMethod http://localhost:8080/api/reportes/morosidad
Invoke-RestMethod http://localhost:8080/api/reportes/flujo-caja-diario
```

Para probar exportacion Excel abre esta URL en el navegador:

```text
http://localhost:8080/api/reportes/deudas/export/excel
```

## 10. Compilar y ejecutar pruebas automaticas

```powershell
.\mvnw.cmd test
```

Si Maven termina con `BUILD SUCCESS`, el proyecto compila y las pruebas
automaticas pasaron correctamente.
