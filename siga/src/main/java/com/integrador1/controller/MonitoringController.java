package com.integrador1.controller;

import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    private final EquipmentService equipmentService;
    private final RentalService rentalService;
    private final MaintenanceService maintenanceService;

    public MonitoringController(
            EquipmentService equipmentService,
            RentalService rentalService,
            MaintenanceService maintenanceService) {

        this.equipmentService = equipmentService;
        this.rentalService = rentalService;
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public String monitoring(Model model){

        model.addAttribute("equipments",
                equipmentService.listarEquipos());

        model.addAttribute("rentals",
                rentalService.listRentals());

        model.addAttribute("maintenances",
                maintenanceService.listMaintenance());

        model.addAttribute("equipmentCount",
                equipmentService.listarEquipos().size());

        model.addAttribute("activeRentals",
                rentalService.countActiveRentals());

        model.addAttribute("openMaintenances",
                maintenanceService.countOpenMaintenances());


        return "monitoring";
    }
}
