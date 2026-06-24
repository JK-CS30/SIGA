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

    // Calcula el siguiente código secuencial disponible
    public String getNextCode() {
        return repository.findFirstByOrderByIdDesc()
                .map(eq -> String.format("G-%03d", eq.getId() + 1))
                .orElse("G-001");
    }

    // Registra un nuevo equipo
    public Equipment registerEquipment(Equipment equipment) {
        // Corrección agregada previamente para limpiar cadenas vacías además de nulos
        if (equipment.getStatus() == null || equipment.getStatus().isBlank()) {
            equipment.setStatus("DISPONIBLE");
        }
        Equipment savedEquipment = repository.save(equipment);
        savedEquipment.setCode(String.format("G-%03d", savedEquipment.getId()));
        return repository.save(savedEquipment);
    }

    // Lista todos los equipos físicamente en la BD (Mantenido para compatibilidad general)
    public List<Equipment> getAllEquipment() {
        return repository.findAll();
    }

    //Lista solo los equipos activos
    public List<Equipment> getActiveEquipment() {
        return repository.findByStatusNotOrderByIdDesc("ELIMINADO");
    }

    //Lista solo los equipos dados de baja
    public List<Equipment> getDeletedEquipment() {
        return repository.findByStatusOrderByIdDesc("ELIMINADO");
    }

    // Obtiene un equipo por su ID
    public Equipment getEquipmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    //Elimina un equipo
    public void deleteEquipment(Long id) {
        // 1. Buscar el equipo por su ID
        Equipment equipment = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        // 2. Cambiar su estado a uno que represente la baja del equipo
        equipment.setStatus("ELIMINADO");
        
        // 3. Guardar el cambio (esto ejecutará un UPDATE en lugar de un DELETE)
        repository.save(equipment);
    }

    // Actualiza un equipo existente
    public Equipment updateEquipment(Long id, Equipment equipment, byte[] newImage) {
        Equipment existingEquipment = getEquipmentById(id);

        existingEquipment.setBrand(equipment.getBrand());
        existingEquipment.setSerialNumber(equipment.getSerialNumber());
        existingEquipment.setYear(equipment.getYear());
        existingEquipment.setUsageIndicator(equipment.getUsageIndicator());
        
        // Nota importante: Si se edita el equipo, mantenemos su estado actual 
        // para que no se resetee a "DISPONIBLE" si ya estaba Ocupado o en Mantenimiento.
        if (equipment.getStatus() != null && !equipment.getStatus().isBlank()) {
            existingEquipment.setStatus(equipment.getStatus());
        }
        
        if (newImage != null && newImage.length > 0) {
            existingEquipment.setImagen(newImage);
        }

        return repository.save(existingEquipment);
    }
}