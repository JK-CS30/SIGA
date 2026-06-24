package com.integrador1.service;

import com.integrador1.model.Maintenance;
import com.integrador1.repository.EquipmentRepository;
import com.integrador1.repository.MaintenanceRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, EquipmentRepository equipmentRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public Maintenance registerMaintenance(Maintenance maintenance){
        if (maintenance.getCost() == null) {
            maintenance.setCost(java.math.BigDecimal.ZERO);
        }
        
        maintenance.setEntryDate(LocalDate.now()); 
        maintenance.setExitDate(null);
        maintenance.setStatus("Planificado"); 
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public void startMaintenance(Long id){
        Maintenance maintenance = getMaintenance(id);
        if (!"Planificado".equals(maintenance.getStatus())) {
            throw new IllegalStateException("Solo se pueden iniciar mantenimientos planificados");
        }
        
        maintenance.setEntryDate(LocalDate.now());
        maintenance.setStatus("En Mantenimiento");
        maintenanceRepository.save(maintenance);

        // STATUS: MANTENIMIENTO
        if (maintenance.getEquipment() != null) {
            var equipment = maintenance.getEquipment();
            equipment.setStatus("MANTENIMIENTO"); 
            equipmentRepository.save(equipment);
        }
    }

    @Transactional
    public void closeMaintenance(Long id){
        Maintenance maintenance = getMaintenance(id);
        maintenance.setExitDate(LocalDate.now());
        maintenance.setStatus("Completado"); 
        maintenanceRepository.save(maintenance);

        // STATUS: DISPONIBLE
        if (maintenance.getEquipment() != null) {
            var equipment = maintenance.getEquipment();
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment);
        }
    }

    @Transactional
    public void cancelMaintenance(Long id){
        Maintenance maintenance = getMaintenance(id);
        if (maintenance.getEntryDate() == null) {
            maintenance.setEntryDate(LocalDate.now()); 
        }
        maintenance.setExitDate(LocalDate.now());
        maintenance.setStatus("Cancelado");
        maintenanceRepository.save(maintenance);

        // STATUS: DISPONIBLE
        if (maintenance.getEquipment() != null) {
            var equipment = maintenance.getEquipment();
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment);
        }
    }

    public List<Maintenance> listMaintenance(){
        return maintenanceRepository.findAll();
    }

    public Maintenance getMaintenance(Long id){
        return maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Mantenimiento no encontrado"));
    }

    @Transactional
    public void deleteMaintenance(Long id){
        maintenanceRepository.deleteById(id);
    }

    @Transactional
    public Maintenance updateMaintenance(Long id, Maintenance maintenance){
        Maintenance existing = getMaintenance(id);

        existing.setType(maintenance.getType());
        existing.setDescription(maintenance.getDescription());
        existing.setUsageIndicator(maintenance.getUsageIndicator());
        existing.setObservations(maintenance.getObservations());
        existing.setNextMaintenanceUsage(maintenance.getNextMaintenanceUsage());
        
        // Al actualizar, si mandan un costo nulo, le asignamos cero
        if (maintenance.getCost() == null) {
            existing.setCost(java.math.BigDecimal.ZERO);
        } else {
            existing.setCost(maintenance.getCost());
        }
        
        if (maintenance.getEquipment() != null) {
            existing.setEquipment(maintenance.getEquipment());
        }
        if (maintenance.getTecnico() != null) {
            existing.setTecnico(maintenance.getTecnico());
        }

        return maintenanceRepository.save(existing);
    }

    public long countOpenMaintenances() {
        return maintenanceRepository.countByStatus("En Mantenimiento");
    }

}