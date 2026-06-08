package com.integrador1.service;

import com.integrador1.model.Equipment;
import com.integrador1.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository repository;

    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
    }

    public Equipment registrarEquipo(Equipment equipment) {

        // Guarda primero para obtener el ID generado
        Equipment savedEquipment = repository.save(equipment);

        // Genera el código G-001, G-002, etc.
        savedEquipment.setCode(
                String.format("G-%03d", savedEquipment.getId())
        );

        return repository.save(savedEquipment);
    }

    public List<Equipment> listarEquipos() {
        return repository.findAll();
    }

    public Equipment obtenerEquipo(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Equipo no encontrado"));
    }

    public void eliminarEquipo(Long id) {
        repository.deleteById(id);
    }

    public Equipment actualizarEquipo(
            Long id,
            Equipment equipment) {

        Equipment existente = obtenerEquipo(id);

        existente.setBrand(equipment.getBrand());
        existente.setSerialNumber(equipment.getSerialNumber());
        existente.setYear(equipment.getYear());
        existente.setHourMeter(equipment.getHourMeter());
        existente.setMileage(equipment.getMileage());

        return repository.save(existente);
    }
    
}