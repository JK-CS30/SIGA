package com.integrador1.controller;

import com.integrador1.model.Rental;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MyAppUserService;
import com.integrador1.service.RentalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RentalController {

    private final RentalService rentalService;
    private final EquipmentService equipmentService;
    private final MyAppUserService myAppUserService;

    public RentalController(
            RentalService rentalService,
            EquipmentService equipmentService,
            MyAppUserService myAppUserService) {

        this.rentalService = rentalService;
        this.equipmentService = equipmentService;
        this.myAppUserService = myAppUserService;
    }

    @GetMapping("/rental")
    public String listRentals(Model model){

        model.addAttribute(
                "rentals",
                rentalService.listRentals());

        model.addAttribute(
                "equipments",
                equipmentService.listarEquipos());

        model.addAttribute(
                "operators",
                myAppUserService.listarUsuarios());

        return "rental";
    }

    @PostMapping("/rental/save")
    public String saveRental(
            Rental rental){

        rentalService.registerRental(rental);

        return "redirect:/rental";
    }

    @PostMapping("/rental/update/{id}")
    public String updateRental(
            @PathVariable Long id,
            Rental rental){

        rentalService.updateRental(
                id,
                rental);

        return "redirect:/rental";
    }

    @DeleteMapping("/rental/delete/{id}")
    @ResponseBody
    public void deleteRental(
            @PathVariable Long id){

        rentalService.deleteRental(id);
    }

    @PostMapping("/rental/close/{id}")
    public String closeRental(
            @PathVariable Long id){

        rentalService.closeRental(id);

        return "redirect:/rental";
    }
}