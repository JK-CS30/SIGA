package com.integrador1.controller;

import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

private final EquipmentService equipmentService;
    private final MaintenanceService maintenanceService;
    private final RentalService rentalService;

    public DashboardController(
            EquipmentService equipmentService,
            MaintenanceService maintenanceService,
            RentalService rentalService) {

        this.equipmentService = equipmentService;
        this.maintenanceService = maintenanceService;
        this.rentalService = rentalService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Métrica de Equipos Totales
        long totalEquipments = equipmentService.getAllEquipment()
                .stream()
                .filter(e -> !"ELIMINADO".equals(e.getStatus())) 
                .count();
        // Lo casteamos a int para mantener el tipo de dato que tenías originalmente
        model.addAttribute("totalEquipments", (int) totalEquipments);

        // Métrica de Alquileres Activos mediante
        long activeRentals = rentalService.listRentals()
                .stream()
                .filter(r -> "ACTIVO".equals(r.getStatus()))
                .count();
        model.addAttribute("activeRentals", activeRentals);

        // Métrica de Mantenimientos Abiertos
        model.addAttribute(
                "openMaintenances",
                maintenanceService.listMaintenance()
                        .stream()
                        .filter(m -> "En Mantenimiento".equals(m.getStatus()))
                        .count());

        // Total de ingresos del mes calculados desde el servicio
        Double total_Amount = rentalService.getTotalIngresosMesActual();
        model.addAttribute("total_Amount", total_Amount);

        // CÁLCULO DE OCUPACIÓN DE INVENTARIO
        double ocupiedEquipment = 0.0;
        if (totalEquipments > 0) {
                // Forzamos decimales multiplicando por 100.0
                ocupiedEquipment = (activeRentals * 100.0) / totalEquipments;
        }
        // Pasamos el valor redondeado al modelo para que sea un número entero limpio
        model.addAttribute("ocupiedEquipment", Math.round(ocupiedEquipment));

        // Estado de página activa para que el Sidebar ilumine el módulo correcto
        model.addAttribute("activePage", "dashboard");

        return "dashboard";

        }
}