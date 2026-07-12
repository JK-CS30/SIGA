package com.integrador1.repository;

import com.integrador1.model.Equipment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
        Optional<Equipment> findFirstByOrderByIdDesc();

        // CONSULTA PARA EQUIPOS CUYO ESTADO SEA DIFERENTE DE ELIMINADO
        List<Equipment> findByStatusNotOrderByIdDesc(String status);

        // CONSULTA PARA EQUIPOS DE ESTADO ELIMINADO(AUDITORIA INTERNA)
        List<Equipment> findByStatusOrderByIdDesc(String status);

        // Equipos operativos
        Long countByStatusNot(String status);

        // Cuenta cuántos están ocupados actualmente
        Long countByStatus(String status);
}