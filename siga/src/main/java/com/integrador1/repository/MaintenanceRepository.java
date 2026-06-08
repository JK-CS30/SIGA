package com.integrador1.repository;

import com.integrador1.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository
        extends JpaRepository<Maintenance, Long> {
}