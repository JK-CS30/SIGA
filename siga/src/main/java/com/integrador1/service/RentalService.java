package com.integrador1.service;

import com.integrador1.model.Equipment;
import com.integrador1.model.Rental;
import com.integrador1.repository.EquipmentRepository;
import com.integrador1.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    // Inyeccion del repositorio de equipos
    private final EquipmentRepository equipmentRepository;

    public RentalService(RentalRepository rentalRepository, EquipmentRepository equipmentRepository) {
        this.rentalRepository = rentalRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public void save(Rental rental) {
        rentalRepository.save(rental);
    }

    public Rental registerRental(Rental rental) {
        rental.setDate(LocalDate.now());
        rental.setStatus("ACTIVO");
        rental.setTotalAmount(0.0);

        if (rental.getEquipment() != null && rental.getEquipment().getId() != null) {
            Equipment equipment = equipmentRepository.findById(rental.getEquipment().getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            equipment.setStatus("ALQUILADO"); // O el término que manejes: "OCUPADO", "EN_OBRA"
            equipmentRepository.save(equipment);
        }

        return rentalRepository.save(rental);
    }

    public List<Rental> listRentals() {
        return rentalRepository.findByStatusNotOrderByIdDesc("ELIMINADO");
    }

    public List<Rental> listDeletedRentals() {
        return rentalRepository.findByStatusOrderByIdDesc("ELIMINADO");
    }

    public Rental getRental(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
    }

    @Transactional
    public void deleteRental(Long id) {
        Rental rental = getRental(id);
        rental.setStatus("ELIMINADO"); // Borrado lógico corporativo

        if (rental.getEquipment() != null) {
            Equipment equipment = rental.getEquipment();
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment);
        }

        rentalRepository.save(rental);
    }

    @Transactional
    public void closeRental(Long id, double totalAmount, String metodoPago, String responsableCobro, String observaciones) {
        Rental rental = getRental(id);
        rental.setTotalAmount(totalAmount);
        
        String flujoCaja = "Flujo: " + metodoPago;
        
        if ("EFECTIVO".equalsIgnoreCase(metodoPago)) {
            rental.setStatus("FINALIZADO");
        } else if ("DEPOSITO".equalsIgnoreCase(metodoPago)) {
            rental.setStatus("PENDIENTE_DEPOSITO");
        } else if ("COBRO_TERCERO".equalsIgnoreCase(metodoPago)) {
            rental.setStatus("PENDIENTE_COBRO");
            flujoCaja += " (Encargado: " + responsableCobro + ")";
        }

        if (observaciones != null && !observaciones.isBlank()) {
            flujoCaja += " | Obs: " + observaciones;
        }
        rental.setObservaciones(flujoCaja);

        //Liberacion del equipo
        if (rental.getEquipment() != null && rental.getEquipment().getId() != null) {
            Equipment equipment = equipmentRepository.findById(rental.getEquipment().getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado para liberación"));
            
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment); // Persistencia inmediata del estado libre
        }

        rentalRepository.save(rental);
    }

    @Transactional
    public void confirmDeposit(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_DEPOSITO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones() );
            rentalRepository.save(rental);
        }
    }

    @Transactional
    public void confirmThirdPartyCollection(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_COBRO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones());
            rentalRepository.save(rental);
        }
    }

    public Rental updateRental(Long id, Rental rental) {
        Rental existingRental = getRental(id);
        existingRental.setCustomerName(rental.getCustomerName());
        existingRental.setServiceDescription(rental.getServiceDescription());
        existingRental.setEquipment(rental.getEquipment());
        existingRental.setOperator(rental.getOperator());
        return rentalRepository.save(existingRental);
    }

    public long countActiveRentals() {
        return rentalRepository.countByStatus("ACTIVO");
    }
    
}