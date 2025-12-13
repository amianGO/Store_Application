# 🔒 Mejoras de Seguridad Post-Despliegue

## ⚠️ IMPORTANTE: Actualizar CORS

### Problema Actual
El `SecurityConfig.java` actual usa `setAllowedOriginPatterns(Arrays.asList("*"))`, lo cual permite requests desde **cualquier dominio**. Esto es inseguro en producción.

### ✅ Solución Recomendada

**Archivo:** `inventory_app/src/main/java/com/example/inventory_app/Config/SecurityConfig.java`

**Buscar el método `corsConfigurationSource()` y reemplazar:**

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // ⚠️ ANTES (INSEGURO):
    // configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    
    // ✅ DESPUÉS (SEGURO):
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173",           // Desarrollo local
        "http://localhost:5174",           // Desarrollo local (backup)
        "https://tu-app.vercel.app",       // ← REEMPLAZAR con tu URL de Vercel
        "https://tu-app-git-*.vercel.app"  // ← Preview deployments de Vercel
    ));
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 📝 Pasos para Actualizar

1. **Obtener tu URL de Vercel:**
   - Después de desplegar en Vercel, copia la URL (ej: `https://store-app-xyz.vercel.app`)

2. **Actualizar el código:**
   ```bash
   # Abre SecurityConfig.java
   # Reemplaza el método corsConfigurationSource() con el código de arriba
   # Reemplaza "tu-app" con tu nombre real de Vercel
   ```

3. **Commit y push:**
   ```bash
   git add inventory_app/src/main/java/com/example/inventory_app/Config/SecurityConfig.java
   git commit -m "security: restrict CORS to specific domains"
   git push origin main
   ```

4. **Render redespleará automáticamente** (5-10 minutos)

---

## 🔑 Variables de Entorno Sensibles

### ✅ Buenas Prácticas

1. **JWT_SECRET_KEY debe ser único y fuerte:**
   ```bash
   # Generar con al menos 32 caracteres
   openssl rand -base64 32
   ```

2. **NUNCA commitear archivos .env:**
   ```bash
   # Verifica que .env esté en .gitignore
   echo ".env" >> .gitignore
   echo ".env.local" >> .gitignore
   echo ".env.production" >> .gitignore
   ```

3. **Rotar secrets periódicamente:**
   - Cambia JWT_SECRET_KEY cada 3-6 meses
   - Cambia Gmail App Password si lo compartes accidentalmente

---

## 📊 Base de Datos

### ✅ Backups Automáticos (Plan Free)

Render NO hace backups automáticos en el plan gratuito. Opciones:

1. **Upgrade a plan de pago** ($7/mes para backups diarios)

2. **Backups manuales periódicos:**
   ```bash
   # Semanal/Mensual
   pg_dump "postgresql://user:pass@host:5432/db" > backup_$(date +%Y%m%d).sql
   ```

3. **Automatizar con cron (local):**
   ```bash
   # Agregar a crontab -e
   0 2 * * 0 pg_dump "postgresql://..." > ~/backups/store_$(date +\%Y\%m\%d).sql
   ```

### ⚠️ Límites del Plan Free

- **Storage:** 500MB máximo
- **Connections:** 97 conexiones simultáneas
- **Retention:** BD se elimina después de 90 días de inactividad

**Monitorea el uso:**
```
Render Dashboard → store-database → Metrics
```

---

## 🔐 Autenticación y Sesiones

### ✅ JWT Expiration

**Actual:** `jwt.expiration=86400000` (24 horas)

**Recomendaciones por rol:**

```properties
# En application-prod.properties

# Para empleados (sesiones de trabajo largas)
jwt.expiration=28800000  # 8 horas

# Para empresas (login menos frecuente)
jwt.empresa.expiration=86400000  # 24 horas
```

### ✅ Refresh Tokens (Futuro)

Considera implementar refresh tokens para sesiones más largas sin comprometer seguridad:
- Access token: 15 minutos
- Refresh token: 7 días

---

## 🚨 Monitoreo y Alertas

### Logs de Seguridad

**Eventos a monitorear:**
1. Intentos de login fallidos (posibles ataques de fuerza bruta)
2. Creación de schemas nuevos (nuevas empresas)
3. Cambios de roles de empleados
4. Accesos desde IPs inusuales

**Implementación básica:**

```java
// En AuthController.java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        // ... lógica de login
        logger.info("Login exitoso: usuario={}, ip={}", request.getUsername(), getClientIp());
    } catch (AuthenticationException e) {
        logger.warn("Login fallido: usuario={}, ip={}, error={}", 
            request.getUsername(), getClientIp(), e.getMessage());
        throw e;
    }
}
```

### Alertas de Base de Datos

**Configura alertas cuando:**
- Storage > 400MB (80% del límite)
- Connections > 80 (82% del límite)
- Respuesta lenta (> 1 segundo)

Render permite configurar webhooks para estas alertas.

---

## 🛡️ Validación de Datos

### ✅ Sanitización de Inputs

**Productos, Clientes, Facturas:**
- Validar caracteres especiales en nombres
- Limitar longitud de campos
- Prevenir SQL injection (JPA lo hace automáticamente, pero verifica queries nativas)

**Ejemplo con Spring Validation:**

```java
public class ProductoDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "Nombre debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-áéíóúÁÉÍÓÚñÑ]+$", 
             message = "Nombre contiene caracteres inválidos")
    private String nombre;
    
    @DecimalMin(value = "0.01", message = "Precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "Precio excede el límite")
    private BigDecimal precio;
}
```

---

## 🔄 Rate Limiting (Futuro)

Para prevenir abuso de la API:

### Opción 1: Spring Rate Limiter

```xml
<!-- En pom.xml -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.0</version>
</dependency>
```

### Opción 2: Cloudflare (Gratis)

- Poner tu dominio detrás de Cloudflare
- Activar "Rate Limiting Rules" (5 reglas gratis)
- Configurar: máximo 100 requests por minuto por IP

---

## 📱 HTTPS y Certificados

### ✅ Render y Vercel

**Buenas noticias:** Ambos proveen HTTPS automáticamente con certificados Let's Encrypt.

**No necesitas configurar nada**, pero verifica:

1. **Todas las URLs usan HTTPS:**
   - ✅ `https://tu-backend.onrender.com`
   - ✅ `https://tu-app.vercel.app`
   - ❌ `http://...` (inseguro)

2. **Frontend NO hace requests HTTP:**
   ```javascript
   // En axios.js
   const API_URL = import.meta.env.VITE_API_URL;
   
   // Verificar que siempre sea https://
   if (API_URL.startsWith('http://') && import.meta.env.PROD) {
       console.error('⚠️ API_URL debe usar HTTPS en producción');
   }
   ```

---

## 🧪 Testing de Seguridad

### Checklist Pre-Producción

- [ ] CORS solo permite dominios específicos
- [ ] Variables de entorno no están hardcodeadas en el código
- [ ] .env no está commiteado en Git
- [ ] JWT_SECRET_KEY tiene al menos 32 caracteres
- [ ] Gmail App Password es específico de la app (no password real)
- [ ] HTTPS habilitado en frontend y backend
- [ ] Logs no muestran información sensible (passwords, tokens)
- [ ] Validación de inputs en todos los endpoints
- [ ] @PreAuthorize en endpoints sensibles
- [ ] Schemas de tenants están aislados (no hay cross-contamination)

### Herramientas de Testing

**1. OWASP ZAP (Gratis):**
```bash
# Escanear tu aplicación en busca de vulnerabilidades
docker run -t owasp/zap2docker-stable zap-baseline.py \
    -t https://tu-app.vercel.app
```

**2. Postman:**
- Probar endpoints sin JWT → Debe retornar 401
- Probar con JWT expirado → Debe retornar 401
- Probar acceder a datos de otro tenant → Debe retornar 403

**3. Browser DevTools:**
- Verificar que tokens NO estén en localStorage visible (usar httpOnly cookies idealmente)
- Verificar que no haya leaks de información sensible en responses

---

## 📈 Escalabilidad (Futuro)

Cuando necesites crecer:

### Render Paid Plans
- **Starter ($7/mes por servicio):** Sin sleep, métricas avanzadas
- **Standard ($25/mes por servicio):** Autoscaling, más recursos

### Base de Datos
- **Render Managed DB ($7/mes):** Backups diarios, 1GB storage
- **Migrate to AWS RDS:** Mayor capacidad, multi-AZ

### CDN
- **Cloudflare (Gratis):** Cache, DDoS protection
- **Vercel Edge Network:** Ya incluido, global CDN

---

## 📝 Checklist de Seguridad

### Inmediato (Antes de usar en producción)
- [ ] Actualizar CORS en SecurityConfig.java
- [ ] Verificar que .env no está en Git
- [ ] JWT_SECRET_KEY fuerte y único
- [ ] HTTPS en todas las URLs

### Corto Plazo (Primera semana)
- [ ] Configurar backups manuales de BD
- [ ] Implementar logs de seguridad
- [ ] Validación de inputs con @Valid
- [ ] Testing con Postman

### Mediano Plazo (Primer mes)
- [ ] Rate limiting
- [ ] Refresh tokens
- [ ] Monitoreo de métricas
- [ ] Alertas de storage/connections

### Largo Plazo (3-6 meses)
- [ ] Rotar JWT_SECRET_KEY
- [ ] Auditoría de seguridad completa
- [ ] Plan de respuesta a incidentes
- [ ] Backup automation

---

## 🆘 Plan de Respuesta a Incidentes

### Si detectas acceso no autorizado:

1. **Inmediato (5 minutos):**
   - Rotar JWT_SECRET_KEY en Render (invalida todos los tokens)
   - Cambiar DATABASE_URL password
   - Revisar logs de Render/Vercel

2. **Corto Plazo (1 hora):**
   - Identificar el vector de ataque
   - Parchear la vulnerabilidad
   - Redeployar con fix

3. **Seguimiento (24 horas):**
   - Notificar a usuarios afectados (si aplica)
   - Documentar el incidente
   - Implementar prevenciones adicionales

---

## 📚 Recursos de Seguridad

- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **Spring Security Docs:** https://docs.spring.io/spring-security/reference/
- **JWT Best Practices:** https://tools.ietf.org/html/rfc8725
- **Render Security:** https://render.com/docs/security

---

**Última actualización:** 2025
**Próxima revisión:** Después del primer mes en producción
