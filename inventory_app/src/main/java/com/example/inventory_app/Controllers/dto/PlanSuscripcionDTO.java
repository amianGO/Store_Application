package com.example.inventory_app.Controllers.dto;

import com.example.inventory_app.Entities.TipoPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar información de planes de suscripción.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanSuscripcionDTO {
    
    private String codigo;
    private String nombre;
    private String descripcion;
    private Double precioMensual;
    private Integer maxTerminales;
    private Integer maxProductos;
    private Integer maxEmpleados;
    private Boolean esGratis;
    private Integer diasPrueba;
    
    /**
     * Convierte un TipoPlan a DTO.
     */
    public static PlanSuscripcionDTO fromTipoPlan(TipoPlan tipoPlan) {
        PlanSuscripcionDTO dto = new PlanSuscripcionDTO();
        dto.setCodigo(tipoPlan.name());
        dto.setNombre(tipoPlan.getNombre());
        dto.setDescripcion(generarDescripcion(tipoPlan));
        dto.setPrecioMensual(tipoPlan.getPrecioMensual());
        dto.setMaxTerminales(tipoPlan.getMaxTerminales());
        dto.setMaxProductos(tipoPlan.getMaxProductos());
        dto.setMaxEmpleados(tipoPlan.getMaxEmpleados());
        dto.setEsGratis(tipoPlan == TipoPlan.PRUEBA);
        dto.setDiasPrueba(tipoPlan == TipoPlan.PRUEBA ? 15 : null);
        return dto;
    }
    
    /**
     * Genera descripción del plan según sus características.
     */
    private static String generarDescripcion(TipoPlan plan) {
        return String.format("Plan %s con hasta %d terminales, %d productos y %d empleados",
            plan.getNombre(),
            plan.getMaxTerminales(),
            plan.getMaxProductos(),
            plan.getMaxEmpleados()
        );
    }
}