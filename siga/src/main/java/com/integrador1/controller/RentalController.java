package com.integrador1.controller;

import com.integrador1.model.Rental;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MyAppUserService;
import com.integrador1.service.RentalService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public String listRentals(@RequestParam(value = "status", required = false) String status, Model model) {
        List<Rental> rentals;

        if (status == null || status.isBlank() || "TODOS".equalsIgnoreCase(status)) {
            rentals = rentalService.listRentals(); //Alquiler activo
        } else if ("PENDIENTE".equalsIgnoreCase(status)) {
            rentals = rentalService.listPendientes(); //Alquiler pendiente
        } else {
            rentals = rentalService.listByStatus(status.toUpperCase()); // Alquiler activo o finalizado
        }

        model.addAttribute("rentals", rentals);
        model.addAttribute("equipments", equipmentService.getAllEquipment());
        model.addAttribute("operators", myAppUserService.listarUsuarios());
        
        // Guardamos el estado actual en el modelo para saber qué botón iluminar en la vista
        model.addAttribute("currentStatus", status != null ? status.toUpperCase() : "TODOS");
        
        return "rental";
    }

    @PostMapping("/rental/save")
    public String saveRental(Rental rental, Authentication authentication){
        Boolean isOwner = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("OWNER"));
        if (!isOwner || rental.getDate() == null){
            rental.setDate(LocalDate.now());
        }
        rentalService.registerRental(rental);
        return "redirect:/rental";
    }

    @PostMapping("/rental/close/{id}")
    public String closeRental(@PathVariable Long id, 
                               @RequestParam Double totalAmount,
                               @RequestParam String paymentMethod,
                               @RequestParam(required = false) String collectionsResponsible,
                               @RequestParam(required = false) String observaciones){
        rentalService.closeRental(id, totalAmount, paymentMethod, collectionsResponsible, observaciones);
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

    @PostMapping("/confirm-deposit/{id}")
    public String confirmarDeposito(
            @PathVariable Long id,
            @RequestParam("montoFinal") BigDecimal montoFinal,
            @RequestParam(value = "observacionesAjuste", required = false) String observacionesAjuste) {
        
        Rental rental = rentalService.getRental(id);
        
        // Si el administrador modificó el monto en la ventana, se actualiza
        rental.setTotalAmount(montoFinal.doubleValue());
        
        // Si dejó una nota de por qué cambió el monto, la concatenamos a las observaciones
        if (observacionesAjuste != null && !observacionesAjuste.isBlank()) {
            rental.setObservaciones(rental.getObservaciones() + " | Ajuste: " + observacionesAjuste);
        }
        
        rental.setStatus("FINALIZADO");
        rentalService.updateRental(id, rental);
        
        return "redirect:/rental";
    }

    @GetMapping("/rental/detail/{id}")
    @ResponseBody
    public ResponseEntity<Rental> getRentalDetail(@PathVariable Long id) {
        // Buscamos el alquiler por su ID usando el servicio
        return rentalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
}