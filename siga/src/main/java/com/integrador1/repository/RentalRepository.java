package com.integrador1.repository;

import com.integrador1.model.Rental;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository
        extends JpaRepository<Rental, Long> {
    long countByStatus(String status);

    // Listar activos, finalizados y pendientes omitiendo los eliminados lógicamente
    List<Rental> findByStatusNotOrderByIdDesc(String status);

    // Listar únicamente los eliminados lógicamente para el módulo de auditoría
    List<Rental> findByStatusOrderByIdDesc(String status);
}