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

    public RentalController(RentalService rentalService, EquipmentService equipmentService, MyAppUserService myAppUserService) {
        this.rentalService = rentalService;
        this.equipmentService = equipmentService;
        this.myAppUserService = myAppUserService;
    }

    @GetMapping("/rental")
    public String listRentals(Model model){
        model.addAttribute("rentals", rentalService.listRentals());
        model.addAttribute("equipments", equipmentService.getAllEquipment());
        model.addAttribute("operators", myAppUserService.listarUsuarios());
        return "rental";
    }

    @PostMapping("/rental/save")
    public String saveRental(Rental rental){
        rentalService.registerRental(rental);
        return "redirect:/rental";
    }

    @PostMapping("/rental/close/{id}")
    public String closeRental(@PathVariable Long id, 
                               @RequestParam double totalAmount,
                               @RequestParam String metodoPago,
                               @RequestParam(required = false) String responsableCobro,
                               @RequestParam(required = false) String observaciones){
        rentalService.closeRental(id, totalAmount, metodoPago, responsableCobro, observaciones);
        return "redirect:/rental";
    }

    @PostMapping("/rental/confirm-deposit/{id}")
    public String confirmDeposit(@PathVariable Long id) {
        rentalService.confirmDeposit(id);
        return "redirect:/rental";
    }

    @PostMapping("/rental/confirm-third-party/{id}")
    public String confirmThirdPartyCollection(@PathVariable Long id) {
        rentalService.confirmThirdPartyCollection(id);
        return "redirect:/rental";
    }

    @DeleteMapping("/rental/delete/{id}")
    @ResponseBody
    public void deleteRental(@PathVariable Long id){
        rentalService.deleteRental(id);
    }

    @GetMapping("/rental/auditoria")
    public String listRentalAudit(Model model) {
        model.addAttribute("deletedRentals", rentalService.listDeletedRentals());
        return "rental-audit"; 
    }
}