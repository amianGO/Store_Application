package com.example.inventory_app.Config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Proveedor de Conexiones Multi-Tenant para Hibernate.
 * 
 * Esta clase es responsable de proporcionar conexiones a la base de datos
 * configuradas para el schema correspondiente al tenant (empresa) actual.
 * 
 * FUNCIONAMIENTO:
 * 1. Hibernate solicita una conexión para ejecutar una query
 * 2. Este provider obtiene una conexión del pool (DataSource)
 * 3. Cambia el schema de esa conexión usando: SET search_path TO {schema}
 * 4. Retorna la conexión configurada a Hibernate
 * 5. Hibernate ejecuta la query en el schema correcto
 * 6. La conexión se devuelve al pool después de usarse
 * 
 * EJEMPLO EN ACCIÓN:
 * - Usuario de "empresa_123" solicita productos
 * - Tenant actual: "schema_empresa_123"
 * - Se obtiene conexión y se ejecuta: SET search_path TO schema_empresa_123
 * - Query: SELECT * FROM productos → Busca en schema_empresa_123.productos
 * - Aislamiento total de datos garantizado
 * 
 * VENTAJAS DE ESTE ENFOQUE:
 * - Un solo pool de conexiones para todos los tenants (eficiente)
 * - Cambio dinámico de schema por conexión (flexible)
 * - Aislamiento de datos a nivel de base de datos (seguro)
 * - No requiere múltiples DataSources (simple)
 * 
 * IMPORTANTE - INYECCIÓN DE DATASOURCE:
 * - NO usamos @Autowired porque Hibernate crea esta clase antes que Spring
 * - El DataSource se pasa por CONSTRUCTOR desde MultiTenancyConfig
 * - Esto garantiza que el DataSource no sea null
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private static final long serialVersionUID = 1L;

    /**
     * DataSource principal de la aplicación (pool de conexiones).
     * Se inyecta mediante CONSTRUCTOR (no @Autowired) desde MultiTenancyConfig.
     */
    private final DataSource dataSource;

    /**
     * Constructor que recibe el DataSource.
     * 
     * @param dataSource Pool de conexiones configurado por Spring
     * @throws IllegalArgumentException si dataSource es null
     */
    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource no puede ser null");
        }
        this.dataSource = dataSource;
        System.out.println("[CONNECTION-PROVIDER] Inicializado con DataSource: " + dataSource.getClass().getName());
    }

    /**
     * Obtiene una conexión para el tenant especificado.
     * 
     * Este método:
     * 1. Obtiene una conexión del pool
     * 2. Configura el schema (search_path) para el tenant
     * 3. Retorna la conexión lista para usar
     * 
     * @param tenantIdentifier Identificador del schema (ej: "schema_empresa_123")
     * @return Conexión configurada para el schema del tenant
     * @throws SQLException Si hay error al obtener o configurar la conexión
     */
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        System.out.println("[CONNECTION-PROVIDER] >>> getConnection() llamado con tenantIdentifier: '" + tenantIdentifier + "'");
        
        final Connection connection = getAnyConnection();
        
        try {
            // Cambiar al schema del tenant usando PostgreSQL search_path
            // search_path define en qué schemas buscar tablas (similar a PATH en OS)
            connection.createStatement()
                      .execute(String.format("SET search_path TO %s", tenantIdentifier));
            
            // Log para debugging
            System.out.println("[CONNECTION-PROVIDER] ✓ Conexión configurada para schema: " + tenantIdentifier);
            
        } catch (SQLException e) {
            // Si falla el cambio de schema, es crítico - loguear y propagar
            System.err.println("[ERROR] No se pudo configurar el schema: " + tenantIdentifier);
            throw new SQLException("Error al configurar schema para tenant: " + tenantIdentifier, e);
        }
        
        return connection;
    }

    /**
     * Libera una conexión del tenant especificado.
     * 
     * Después de usar una conexión, Hibernate llama este método para devolverla.
     * Antes de devolverla al pool, reseteamos el schema al default (public).
     * 
     * IMPORTANTE: Resetear evita que la próxima operación que use esta conexión
     * del pool herede el schema del tenant anterior (contaminación de contexto).
     * 
     * @param tenantIdentifier Identificador del schema que estaba usando la conexión
     * @param connection Conexión a liberar
     * @throws SQLException Si hay error al resetear o cerrar la conexión
     */
    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            // Resetear al schema público antes de devolver al pool
            connection.createStatement().execute("SET search_path TO public");
            
            // Devolver la conexión al pool (no la cierra, la reutiliza)
            connection.close(); // En realidad retorna al pool de HikariCP
            
        } catch (SQLException e) {
            // Log del error pero no bloqueamos el release
            System.err.println("[WARN] Error al liberar conexión del tenant: " + tenantIdentifier);
            throw e;
        }
    }

    /**
     * Indica si soporta obtener conexiones sin especificar tenant.
     * 
     * @return true - Soportamos obtener conexiones genéricas (para schema public)
     */
    @Override
    public boolean supportsAggressiveRelease() {
        // Aggressive release = liberar conexiones apenas se pueda
        // true = mejor gestión de recursos, conexiones disponibles más rápido
        return true;
    }

    /**
     * Obtiene una conexión genérica del DataSource sin configurar schema específico.
     * Esta conexión usará el schema por defecto (public).
     * 
     * Se usa para:
     * - Operaciones globales (login, registro de empresas)
     * - Consultas al schema público
     * - Base para getConnection(tenantId)
     * 
     * @return Conexión del pool sin schema específico configurado
     * @throws SQLException Si no se puede obtener conexión del pool
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Libera una conexión genérica (sin tenant específico).
     * Simplemente la devuelve al pool.
     * 
     * @param connection Conexión a liberar
     * @throws SQLException Si hay error al liberar la conexión
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close(); // Retorna al pool
    }

    /**
     * Unwrap para exponer el DataSource subyacente si es necesario.
     * Requerido por la interfaz MultiTenantConnectionProvider.
     * 
     * @param unwrapType Clase a la que se quiere hacer unwrap
     * @return El objeto unwrapped si es posible
     */
    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)
                || DataSource.class.isAssignableFrom(unwrapType);
    }

    /**
     * Realiza el unwrap al tipo especificado.
     * 
     * @param unwrapType Clase objetivo del unwrap
     * @return Instancia del tipo solicitado
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> unwrapType) {
        if (MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T) this;
        } else if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T) dataSource;
        } else {
            throw new IllegalArgumentException("No se puede unwrap a: " + unwrapType);
        }
    }
}
