# 🛡️ CONTROL DE PERMISOS - PRODUCTOS

## 📋 Resumen de Permisos

### **ADMIN** (Administrador)
- ✅ **Crear** productos
- ✅ **Ver/Listar** productos
- ✅ **Editar** productos
- ✅ **Eliminar** productos

### **VENDEDOR, CAJERO, INVENTARIO** (Otros roles)
- ✅ **Ver/Listar** productos
- ❌ **Crear** productos (denegado)
- ❌ **Editar** productos (denegado)
- ❌ **Eliminar** productos (denegado)

### **EMPRESA** (Token de empresa)
- ✅ **Crear** productos
- ✅ **Ver/Listar** productos
- ✅ **Editar** productos
- ✅ **Eliminar** productos

---

## 🔐 Implementación Backend

### ProductoController.java

```java
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    // ✅ Listar productos - TODOS los empleados autenticados
    @GetMapping
    public ResponseEntity<?> getAllProductos() { ... }

    // ✅ Crear producto - Solo ADMIN o EMPRESA
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) { ... }

    // ✅ Actualizar producto - Solo ADMIN o EMPRESA
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto producto) { ... }

    // ✅ Eliminar producto - Solo ADMIN o EMPRESA
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) { ... }
}
```

---

## 🎨 Implementación Frontend

### CreateProduct.jsx

```javascript
useEffect(() => {
  const token = localStorage.getItem('token');
  if (token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const rol = payload.rol || '';
    
    // Si no es ADMIN, redirigir
    if (rol !== 'ADMIN') {
      setError('Solo usuarios ADMIN pueden crear productos');
      navigate('/dashboard');
      return;
    }
  }
}, []);
```

---

## 📊 Flujo de Validación

```
Usuario intenta acceder a /productos/create
           ↓
Frontend verifica rol del token JWT
           ↓
   ┌─────────┴──────────┐
   ↓                    ↓
ROL = ADMIN         ROL ≠ ADMIN
   ↓                    ↓
Permite acceso    Redirige a /dashboard
   ↓
Usuario crea producto
   ↓
POST /api/productos
   ↓
Backend verifica @PreAuthorize("hasRole('ADMIN')")
   ↓
   ┌─────────┴──────────┐
   ↓                    ↓
TIENE ROL          NO TIENE ROL
   ↓                    ↓
Crea producto     403 Forbidden
```

---

## ✅ Estado Actual

- ✅ Backend: Permisos configurados con `@PreAuthorize`
- ✅ Frontend: Validación de rol antes de acceder a formulario
- ✅ JWT: Claims incluyen `rol: "ADMIN"` o `rol: "VENDEDOR"`
- ✅ Spring Security: Convierte `rol` a `ROLE_ADMIN` automáticamente
- ✅ Multi-Tenant: Cada empresa tiene sus propios productos aislados

---

## 🚀 Próximos Pasos

1. ✅ Implementar control de permisos en edición de productos
2. ✅ Implementar control de permisos en eliminación de productos
3. ⏳ Agregar control de permisos para clientes
4. ⏳ Agregar control de permisos para ventas
