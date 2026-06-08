package com.integrador1.service;

import com.integrador1.model.Rental;
import com.integrador1.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(
            RentalRepository rentalRepository) {

        this.rentalRepository = rentalRepository;
    }

    public Rental registerRental(
            Rental rental) {

        rental.setDate(LocalDate.now());

        rental.setStatus("ACTIVO");

        return rentalRepository.save(rental);
    }

    public List<Rental> listRentals() {
        return rentalRepository.findAll();
    }

    public Rental getRental(Long id) {

        return rentalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alquiler no encontrado"));
    }

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    public Rental updateRental(
            Long id,
            Rental rental) {

        Rental existingRental =
                getRental(id);

        existingRental.setHourlyRate(
                rental.getHourlyRate());

        existingRental.setTotalAmount(
                rental.getTotalAmount());

        existingRental.setCustomerName(
                rental.getCustomerName());

        existingRental.setCustomerRuc(
                rental.getCustomerRuc());

        existingRental.setStatus(
                rental.getStatus());

        existingRental.setObservaciones(
                rental.getObservaciones());

        existingRental.setEquipment(
                rental.getEquipment());

        return rentalRepository.save(
                existingRental);
    }

    public void closeRental(Long id){

        Rental rental = getRental(id);

        rental.setStatus("FINALIZADO");

        rentalRepository.save(rental);
    }
}