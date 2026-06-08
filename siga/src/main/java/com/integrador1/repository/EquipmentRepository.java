package com.integrador1.repository;

import com.integrador1.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository
        extends JpaRepository<Equipment, Long> {
}