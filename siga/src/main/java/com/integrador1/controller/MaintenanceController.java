package com.integrador1.controller;

import com.integrador1.model.Maintenance;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final EquipmentService equipmentService;

    public MaintenanceController(
            MaintenanceService maintenanceService,
            EquipmentService equipmentService) {

        this.maintenanceService = maintenanceService;
        this.equipmentService = equipmentService;
    }

    @GetMapping("/maintenance")
    public String listMaintenances(Model model){

        model.addAttribute(
                "maintenances",
                maintenanceService.listMaintenance());

        model.addAttribute(
                "equipments",
                equipmentService.getAllEquipment());

        return "maintenance";
    }

    @PostMapping("/maintenance/save")
    public String saveMaintenance(
            Maintenance maintenance){

        maintenanceService.registerMaintenance(maintenance);

        return "redirect:/maintenance";
    }

    @PostMapping("/maintenance/close/{id}")
    public String closeMaintenance(
            @PathVariable Long id){

        maintenanceService.closeMaintenance(id);

        return "redirect:/maintenance";
    }

    @GetMapping("/maintenance/edit/{id}")
    public String editMaintenance(
            @PathVariable Long id,
            Model model){

        model.addAttribute(
                "maintenance",
                maintenanceService.getMaintenance(id)
        );

        model.addAttribute(
                "maintenances",
                maintenanceService.listMaintenance()
        );

        model.addAttribute(
                "equipments",
                equipmentService.getAllEquipment()
        );

        return "maintenance";
    }

    @PostMapping("/maintenance/update/{id}")
    public String updateMaintenance(@PathVariable Long id, Maintenance maintenance){
        maintenanceService.updateMaintenance(id, maintenance);

        return "redirect:/maintenance";
    }

    @DeleteMapping("/maintenance/delete/{id}")
    @ResponseBody
    public void deleteMaintenance(
            @PathVariable Long id){

        maintenanceService.deleteMaintenance(id);
    }

}
