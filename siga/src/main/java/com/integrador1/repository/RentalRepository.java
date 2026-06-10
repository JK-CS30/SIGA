package com.integrador1.repository;

import com.integrador1.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository
        extends JpaRepository<Rental, Long> {
    long countByStatus(String status);
}