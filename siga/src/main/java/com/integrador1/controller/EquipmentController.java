package com.integrador1.controller;

import com.integrador1.model.Equipment;
import com.integrador1.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/equipment")
    public String listEquipment(Model model) {
        // CAMBIO AQUÍ: Ahora pedimos solo los equipos activos (sin los "ELIMINADO")
        model.addAttribute("equitment", equipmentService.getActiveEquipment());
        model.addAttribute("nextCode", equipmentService.getNextCode());
        return "equipment";
    }

    @PostMapping("/equipment/save")
    public String saveEquipment(@ModelAttribute Equipment equipment, 
                                @RequestParam("fileImagen") MultipartFile file) throws IOException {
        
        if (!file.isEmpty()) {
            equipment.setImagen(file.getBytes());
        }
        equipmentService.registerEquipment(equipment);
        return "redirect:/equipment";
    }

    @PostMapping("/equipment/update/{id}")
    public String updateEquipment(@PathVariable Long id,
                                   @ModelAttribute Equipment equipment,
                                   @RequestParam("fileImagen") MultipartFile file) throws IOException {
        
        byte[] imageBytes = file.isEmpty() ? null : file.getBytes();
        equipmentService.updateEquipment(id, equipment, imageBytes);
        return "redirect:/equipment";
    }

    @DeleteMapping("/equipment/delete/{id}")
    @ResponseBody
    public void deleteEquipment(@PathVariable Long id) {
        // El servicio se encargará de hacer el borrado lógico (cambiar estado a ELIMINADO)
        equipmentService.deleteEquipment(id);
    }

    /**
     * ENDPOINT EXTRA PARA AUDITORÍA
     * Ruta para acceder al listado de equipos que fueron dados de baja.
     */
    @GetMapping("/equipment/auditoria")
    public String listAuditEquipment(Model model) {
        model.addAttribute("auditEquipment", equipmentService.getDeletedEquipment());
        return "equipment-audit"; // Nombre de la nueva plantilla HTML para auditoría
    }
}