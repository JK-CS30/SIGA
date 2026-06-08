package com.integrador1.controller;

import com.integrador1.model.Equipment;
import com.integrador1.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/equipment")
    public String listEquipos(Model model){

        model.addAttribute(
                "equitment",
                equipmentService.listarEquipos()
        );

        return "equipment";
    }

    @PostMapping("/equipment/save")
    public String guardarEquipo(Equipment equipment) {

        equipmentService.registrarEquipo(equipment);

        return "redirect:/equipment";
    }

    @GetMapping("/equipment/edit/{id}")
    public String editarEquipo(@PathVariable Long id, Model model) {

        model.addAttribute(
                "equipo",
                equipmentService.obtenerEquipo(id)
        );

        return "equipment";
    }

    @PostMapping("/equipment/update/{id}")
    public String actualizarEquipo(
            @PathVariable Long id,
            Equipment equipment) {

        equipmentService.actualizarEquipo(id, equipment);

        return "redirect:/equipment";
    }

    @DeleteMapping("/equipment/delete/{id}")
    @ResponseBody
    public void eliminarEquipo(@PathVariable Long id) {

        equipmentService.eliminarEquipo(id);
    }

}