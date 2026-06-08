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
    public String dashboard(Model model){

        model.addAttribute(
                "totalEquipments",
                equipmentService.listarEquipos().size());

        model.addAttribute(
                "activeRentals",
                rentalService.listRentals()
                        .stream()
                        .filter(r -> "ACTIVO".equals(r.getStatus()))
                        .count());

        model.addAttribute(
                "openMaintenances",
                maintenanceService.listMaintenance()
                        .stream()
                        .filter(m -> "ABIERTO".equals(m.getStatus()))
                        .count());

        return "dashboard";
    }
}