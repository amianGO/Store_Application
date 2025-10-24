# API Documentation - Sistema de Inventario

## Descripción
API REST para un sistema de gestión de inventario y ventas desarrollado con Spring Boot.

## Base URL
```
http://localhost:8080/api
```

## Endpoints Principales

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar empleado

### Productos
- `GET /api/productos` - Listar todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `GET /api/productos/codigo/{codigo}` - Obtener producto por código
- `GET /api/productos/categoria/{categoria}` - Listar por categoría
- `GET /api/productos/bajo-stock` - Listar productos con bajo stock
- `GET /api/productos/buscar?nombre={nombre}` - Buscar por nombre
- `POST /api/productos` - Crear producto
- `PUT /api/productos/{id}` - Actualizar producto
- `PATCH /api/productos/{id}/stock?cantidad={cantidad}` - Actualizar stock
- `DELETE /api/productos/{id}` - Desactivar producto

### Clientes
- `GET /api/clientes` - Listar clientes activos
- `GET /api/clientes/{id}` - Obtener cliente por ID
- `GET /api/clientes/documento/{documento}` - Obtener por documento
- `GET /api/clientes/buscar?texto={texto}` - Buscar por nombre/apellido
- `POST /api/clientes` - Crear cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Desactivar cliente

### Facturas
- `GET /api/facturas` - Listar facturas
- `GET /api/facturas/{id}` - Obtener factura por ID
- `GET /api/facturas/numero/{numeroFactura}` - Obtener por número
- `GET /api/facturas/cliente/{clienteId}` - Listar por cliente
- `GET /api/facturas/empleado/{empleadoId}` - Listar por empleado
- `GET /api/facturas/fecha?fechaInicio={fecha}&fechaFin={fecha}` - Listar por rango de fechas
- `GET /api/facturas/estado/{estado}` - Listar por estado
- `GET /api/facturas/ventas-dia?fecha={fecha}` - Obtener total de ventas del día
- `POST /api/facturas` - Crear factura
- `PATCH /api/facturas/{id}/anular` - Anular factura

### Carritos de Compra
- `GET /api/carritos` - Listar carritos
- `GET /api/carritos/{id}` - Obtener carrito por ID
- `GET /api/carritos/cliente/{clienteId}` - Listar por cliente
- `GET /api/carritos/empleado/{empleadoId}` - Listar por empleado
- `GET /api/carritos/cliente/{clienteId}/activo` - Obtener carrito activo
- `POST /api/carritos` - Crear carrito
- `PATCH /api/carritos/{id}/actualizar-total` - Actualizar total
- `PATCH /api/carritos/{id}/completar` - Completar carrito
- `DELETE /api/carritos/{id}/vaciar` - Vaciar carrito
- `DELETE /api/carritos/limpiar-abandonados?horas={horas}` - Limpiar carritos abandonados

### Cajas
- `GET /api/cajas` - Listar cajas
- `GET /api/cajas/{id}` - Obtener caja por ID
- `GET /api/cajas/numero/{numeroCaja}` - Obtener por número
- `GET /api/cajas/empleado/{empleadoId}` - Listar por empleado
- `GET /api/cajas/estado/{estado}` - Listar por estado
- `GET /api/cajas/fecha?fecha={fecha}` - Listar por fecha de apertura
- `GET /api/cajas/empleado/{empleadoId}/caja-abierta` - Verificar caja abierta
- `POST /api/cajas` - Abrir caja
- `PATCH /api/cajas/{id}/cerrar?montoFinal={monto}&observaciones={obs}` - Cerrar caja
- `PATCH /api/cajas/{id}/actualizar-ventas?montoVenta={monto}` - Actualizar ventas

### Empleados (Solo Admin)
- `GET /api/empleados` - Listar empleados activos
- `GET /api/empleados/{id}` - Obtener empleado por ID
- `GET /api/empleados/usuario/{usuario}` - Obtener por usuario
- `GET /api/empleados/documento/{documento}` - Obtener por documento
- `GET /api/empleados/cargo/{cargo}` - Listar por cargo
- `POST /api/empleados` - Crear empleado
- `PUT /api/empleados/{id}` - Actualizar empleado
- `DELETE /api/empleados/{id}` - Desactivar empleado

### Estadísticas
- `GET /api/estadisticas/dashboard` - Obtener estadísticas del dashboard
- `GET /api/estadisticas/ventas/dia?fecha={fecha}` - Obtener ventas del día

## Categorías de Productos
- ELECTRONICA
- ROPA
- CALZADO
- ALIMENTOS
- BEBIDAS
- LIMPIEZA
- PAPELERIA
- FERRETERIA
- HOGAR
- JUGUETES
- DEPORTES
- OTROS

## Roles de Empleados
- ADMIN
- VENDEDOR
- INVENTARIO
- CAJERO

## Estados de Facturas
- PENDIENTE
- COMPLETADA
- ANULADA

## Estados de Carritos
- ACTIVO
- COMPLETADO

## Estados de Cajas
- ABIERTA
- CERRADA

## Validaciones
- Todos los campos obligatorios están marcados con `@NotBlank` o `@NotNull`
- Los emails deben tener formato válido
- Las contraseñas deben tener al menos 6 caracteres
- Los precios deben ser mayores a 0
- Los stocks no pueden ser negativos

## CORS
La API está configurada para aceptar peticiones desde cualquier origen (`*`) para facilitar el desarrollo.

## Autenticación
Actualmente configurada con autenticación básica HTTP para pruebas. Los endpoints de autenticación están disponibles públicamente.
