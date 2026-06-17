package com.integrador1.service;

import com.integrador1.model.Rental;
import com.integrador1.repository.RentalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental registerRental(Rental rental) {
        rental.setDate(LocalDate.now());
        rental.setStatus("ACTIVO");
        rental.setTotalAmount(0.0); // Inicia en 0, se define en la liquidación
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

    public void deleteRental(Long id) {
        Rental rental = getRental(id);
        rental.setStatus("ELIMINADO"); // Borrado lógico corporativo
        rentalRepository.save(rental);
    }

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

        rentalRepository.save(rental);
    }

    public void confirmDeposit(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_DEPOSITO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones() + " (DEPÓSITO VERIFICADO EN BANCO)");
            rentalRepository.save(rental);
        }
    }

    public void confirmThirdPartyCollection(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_COBRO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones() + " (COBRO RECAUDADO Y ENTREGADO)");
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