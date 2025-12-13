package com.example.inventory_app.Config;

/**
 * Contexto del Tenant (Empresa) - Almacena el schema actual por hilo de ejecución.
 * 
 * Esta clase utiliza ThreadLocal para mantener el identificador del schema de base de datos
 * correspondiente a la empresa (tenant) que está siendo procesada en el request actual.
 * 
 * IMPORTANTE:
 * - Cada request HTTP se ejecuta en un hilo diferente
 * - ThreadLocal garantiza que cada hilo tenga su propia copia del tenant
 * - Siempre limpiar el contexto después de usar (clear()) para evitar memory leaks
 * 
 * Flujo de uso:
 * 1. Interceptor/Filter identifica la empresa del usuario autenticado
 * 2. Se establece el schema usando setCurrentTenant(schema)
 * 3. Hibernate usa este valor para conectarse al schema correcto
 * 4. Al finalizar el request, se limpia con clear()
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
public class TenantContext {

    /**
     * Schema por defecto cuando no hay tenant identificado.
     * Se usa para operaciones globales (registro de empresas, login inicial, etc.)
     */
    private static final String DEFAULT_TENANT = "public";

    /**
     * ThreadLocal que mantiene el schema actual para cada hilo de ejecución.
     * Cada request HTTP tiene su propio hilo, por lo que cada request puede
     * tener un tenant diferente sin interferencias.
     */
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Constructor privado para prevenir instanciación.
     * Esta es una clase utilitaria con métodos estáticos únicamente.
     */
    private TenantContext() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }

    /**
     * Establece el tenant (schema) actual para el hilo de ejecución actual.
     * 
     * @param tenantId Identificador del schema de la empresa (ej: "empresa_123", "tenant_abc")
     * @throws IllegalArgumentException si tenantId es null o vacío
     */
    public static void setCurrentTenant(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("El tenant ID no puede ser nulo o vacío");
        }
        String normalizedTenant = tenantId.toLowerCase().trim();
        CURRENT_TENANT.set(normalizedTenant);
        
        // Log para debugging
        System.out.println("▼▼▼ [TENANT-CONTEXT] setCurrentTenant() ▼▼▼");
        System.out.println("    Thread: " + Thread.currentThread().getName());
        System.out.println("    Tenant establecido: " + normalizedTenant);
        System.out.println("▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲");
    }

    /**
     * Obtiene el tenant (schema) actual del hilo de ejecución.
     * Si no se ha establecido ningún tenant, retorna el schema por defecto (public).
     * 
     * @return El identificador del schema actual o "public" si no está definido
     */
    public static String getCurrentTenant() {
        String tenant = CURRENT_TENANT.get();
        String result = (tenant != null && !tenant.isEmpty()) ? tenant : DEFAULT_TENANT;
        
        // Log para debugging
        System.out.println("►►► [TENANT-CONTEXT] getCurrentTenant()");
        System.out.println("    Thread: " + Thread.currentThread().getName());
        System.out.println("    ThreadLocal value: " + tenant);
        System.out.println("    Retornando: " + result);
        
        return result;
    }

    /**
     * Limpia el tenant del hilo actual.
     * 
     * CRÍTICO: Siempre llamar este método al finalizar el request para evitar:
     * - Memory leaks en aplicaciones con pool de hilos
     * - Que un tenant "persista" en un hilo reutilizado
     * - Contaminación de datos entre requests
     * 
     * Se debe llamar en un bloque finally o en un interceptor después del request.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /**
     * Verifica si actualmente hay un tenant establecido.
     * 
     * @return true si hay un tenant diferente al default, false en caso contrario
     */
    public static boolean hasTenant() {
        String tenant = CURRENT_TENANT.get();
        return tenant != null && !tenant.isEmpty() && !DEFAULT_TENANT.equals(tenant);
    }

    /**
     * Restablece el tenant al valor por defecto (public).
     * Útil para operaciones que necesitan trabajar con el schema público.
     */
    public static void resetToDefault() {
        CURRENT_TENANT.set(DEFAULT_TENANT);
    }
}
