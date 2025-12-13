package com.example.inventory_app.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Servicio para gestión de schemas de base de datos (Multi-Tenancy).
 * 
 * Responsabilidades:
 * - Crear schemas dedicados para nuevos tenants
 * - Clonar estructura desde template_schema
 * - Eliminar schemas de tenants
 * - Validar existencia de schemas
 * 
 * IMPORTANTE: Este servicio ejecuta comandos DDL directamente en PostgreSQL.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Service
public class SchemaManagementService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Crea un schema dedicado para un tenant y clona la estructura desde template_schema.
     * 
     * Proceso:
     * 1. Verificar que el schema no exista
     * 2. Crear schema nuevo
     * 3. Clonar tablas desde template_schema (si existe)
     * 4. Asignar permisos
     * 
     * @param schemaName Nombre del schema a crear (ej: "empresa_1")
     * @throws RuntimeException si hay error en la creación
     */
    @Transactional
    public void crearSchemaParaTenant(String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Validar nombre del schema (seguridad)
            if (!schemaName.matches("^[a-z0-9_]+$")) {
                throw new IllegalArgumentException("Nombre de schema inválido: " + schemaName);
            }

            // Verificar si el schema ya existe
            if (schemaExiste(schemaName)) {
                System.out.println("[SCHEMA-SERVICE] Schema ya existe: " + schemaName);
                return;
            }

            System.out.println("[SCHEMA-SERVICE] Creando schema: " + schemaName);

            // Paso 1: Crear el schema
            String createSchemaSql = String.format("CREATE SCHEMA IF NOT EXISTS %s", schemaName);
            statement.execute(createSchemaSql);
            System.out.println("[SCHEMA-SERVICE] Schema creado exitosamente");

            // Paso 2: Clonar estructura desde template_schema (si existe)
            if (schemaExiste("template_schema")) {
                clonarEstructuraDesdeTemplate(schemaName, statement);
            } else {
                System.out.println("[SCHEMA-SERVICE] ADVERTENCIA: template_schema no existe. Creando estructura básica...");
                crearEstructuraBasica(schemaName, statement);
            }

            System.out.println("[SCHEMA-SERVICE] Schema " + schemaName + " listo para usar");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error al crear schema: " + e.getMessage());
            throw new RuntimeException("Error al crear schema: " + e.getMessage(), e);
        }
    }

    /**
     * Clona la estructura de tablas desde template_schema al nuevo schema.
     * 
     * @param targetSchema Schema destino
     * @param statement Statement SQL
     * @throws SQLException si hay error
     */
    private void clonarEstructuraDesdeTemplate(String targetSchema, Statement statement) throws SQLException {
        System.out.println("[SCHEMA-SERVICE] Clonando estructura desde template_schema...");

        // Obtener lista de tablas en template_schema
        String[] tablas = {
            "productos",
            "clientes",
            "empleados",
            "facturas",
            "detalle_facturas",
            "carrito_compras",
            "cajas"
        };

        for (String tabla : tablas) {
            try {
                // Clonar estructura de tabla (sin datos)
                String cloneSql = String.format(
                    "CREATE TABLE IF NOT EXISTS %s.%s (LIKE template_schema.%s INCLUDING ALL)",
                    targetSchema, tabla, tabla
                );
                statement.execute(cloneSql);
                System.out.println("[SCHEMA-SERVICE]   ✓ Tabla clonada: " + tabla);
            } catch (SQLException e) {
                System.err.println("[SCHEMA-SERVICE]   ✗ Error al clonar tabla " + tabla + ": " + e.getMessage());
                // Continuar con las demás tablas
            }
        }
    }

    /**
     * Crea la estructura básica de tablas si no existe template_schema.
     * 
     * NOTA: Este método crea una estructura mínima.
     * Lo ideal es tener un template_schema previamente configurado.
     * 
     * @param schemaName Schema donde crear las tablas
     * @param statement Statement SQL
     * @throws SQLException si hay error
     */
    private void crearEstructuraBasica(String schemaName, Statement statement) throws SQLException {
        System.out.println("[SCHEMA-SERVICE] Creando estructura básica en " + schemaName);

        // Nota: Hibernate creará las tablas automáticamente con ddl-auto=update
        // cuando se conecte a este schema por primera vez.
        
        System.out.println("[SCHEMA-SERVICE] La estructura se creará automáticamente mediante Hibernate");
        System.out.println("[SCHEMA-SERVICE] RECOMENDACIÓN: Crear template_schema con la estructura completa");
    }

    /**
     * Verifica si un schema existe en la base de datos.
     * 
     * @param schemaName Nombre del schema
     * @return true si existe
     */
    public boolean schemaExiste(String schemaName) {
        String sql = "SELECT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = ?)";
        Boolean existe = jdbcTemplate.queryForObject(sql, Boolean.class, schemaName);
        return existe != null && existe;
    }

    /**
     * Elimina un schema de tenant (USE CON PRECAUCIÓN).
     * 
     * @param schemaName Nombre del schema a eliminar
     */
    @Transactional
    public void eliminarSchema(String schemaName) {
        // Validación de seguridad: NO permitir eliminar schemas del sistema
        if (schemaName.equals("public") || schemaName.equals("template_schema") || 
            schemaName.equals("pg_catalog") || schemaName.equals("information_schema")) {
            throw new IllegalArgumentException("No se puede eliminar schema del sistema: " + schemaName);
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String dropSchemaSql = String.format("DROP SCHEMA IF EXISTS %s CASCADE", schemaName);
            statement.execute(dropSchemaSql);

            System.out.println("[SCHEMA-SERVICE] Schema eliminado: " + schemaName);

        } catch (SQLException e) {
            System.err.println("[ERROR] Error al eliminar schema: " + e.getMessage());
            throw new RuntimeException("Error al eliminar schema: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos los schemas de tenants en la base de datos.
     * 
     * @return Lista de nombres de schemas
     */
    public java.util.List<String> listarSchemasDeTenants() {
        String sql = "SELECT schema_name FROM information_schema.schemata " +
                     "WHERE schema_name LIKE 'empresa_%' ORDER BY schema_name";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}
