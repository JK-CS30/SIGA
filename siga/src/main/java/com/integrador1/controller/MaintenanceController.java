package com.integrador1.controller;

import com.integrador1.model.Maintenance;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        if (!model.containsAttribute("maintenance")) {
            model.addAttribute("maintenance", new Maintenance());
        }
        model.addAttribute("maintenances", maintenanceService.listMaintenance());
        model.addAttribute("equipments", equipmentService.getAllEquipment());

        return "maintenance";
    }

    @PostMapping("/maintenance/save")
    public String saveMaintenance(@Valid @ModelAttribute("maintenance") Maintenance maintenance, 
                                  BindingResult result, Model model){
        if (result.hasErrors()) {
            model.addAttribute("maintenances", maintenanceService.listMaintenance());
            model.addAttribute("equipments", equipmentService.getAllEquipment());
            return "maintenance";
        }

        maintenanceService.registerMaintenance(maintenance);

        return "redirect:/maintenance";
    }

    @PostMapping("/maintenance/close/{id}")
    public String closeMaintenance(@PathVariable Long id, 
                                    @RequestParam Double cost,
                                    @RequestParam String observations, 
                                    @RequestParam Double nextMaintenanceUsage){

        maintenanceService.closeMaintenance(id, cost, observations, nextMaintenanceUsage);
        return "redirect:/maintenance";
    }

    @GetMapping("/maintenance/edit/{id}")
    public String editMaintenance(@PathVariable Long id, Model model){

        model.addAttribute("maintenance", maintenanceService.getMaintenance(id));
        model.addAttribute("maintenances", maintenanceService.listMaintenance());
        model.addAttribute("equipments", equipmentService.getAllEquipment());

        return "maintenance";
    }

    @PostMapping("/maintenance/update/{id}")
    public String updateMaintenance(@PathVariable Long id, @Valid @ModelAttribute("maintenance") Maintenance maintenance, 
                                    BindingResult result, Model model){
        if (result.hasErrors()) {
            model.addAttribute("maintenances", maintenanceService.listMaintenance());
            model.addAttribute("equipments", equipmentService.getAllEquipment());
            return "maintenance";
        }
        
        maintenanceService.updateMaintenance(id, maintenance);

        return "redirect:/maintenance";
    }

    @DeleteMapping("/maintenance/delete/{id}")
    @ResponseBody
    public void deleteMaintenance(
            @PathVariable Long id){

        maintenanceService.deleteMaintenance(id);
    }

    @PostMapping("/maintenance/start/{id}")
    public String startMaintenance(@PathVariable Long id) {
        maintenanceService.startMaintenance(id);
        return "redirect:/maintenance"; 
    }

    @PostMapping("/maintenance/cancel/{id}")
    public String cancelMaintenance(@PathVariable Long id) {
        maintenanceService.cancelMaintenance(id);
        return "redirect:/maintenance";
    }

    @GetMapping("/maintenance/detail/{id}")
    @ResponseBody
    public ResponseEntity<Maintenance> getMaintenanceDetail(@PathVariable Long id) {
        // Reutilizamos el método findById standard de tu service
        return maintenanceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/maintenance/equipment/{equipmentId}")
    @ResponseBody
    public ResponseEntity<List<Maintenance>> getMaintenanceByEquipment(@PathVariable Long equipmentId){
        List<Maintenance> list = maintenanceService.findByEquipmentId(equipmentId);
        return ResponseEntity.ok(list);
    }
}
