package com.integrador1.service;

import com.integrador1.model.Maintenance;
import com.integrador1.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public Maintenance registerMaintenance(Maintenance maintenance){

        maintenance.setEntryDate(LocalDate.now());

        maintenance.setExitDate(null);

        maintenance.setStatus("ABIERTO");

        return maintenanceRepository.save(maintenance);
    }

    public void closeMaintenance(Long id){

        Maintenance maintenance = getMaintenance(id);

        maintenance.setExitDate(LocalDate.now());
        maintenance.setStatus("CERRADO");

        maintenanceRepository.save(maintenance);
    }

    public List<Maintenance> listMaintenance(){
        return maintenanceRepository.findAll();
    }

    public Maintenance getMaintenance(Long id){
        return maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Mantenimiento no encontrado"));
    }

    public void deleteMaintenance(Long id){
        maintenanceRepository.deleteById(id);
    }

    public Maintenance updateMaintenance(Long id,
                                         Maintenance maintenance){

        Maintenance existing = getMaintenance(id);

        existing.setType(maintenance.getType());
        existing.setDescription(maintenance.getDescription());
        existing.setHourMeter(maintenance.getHourMeter());
        existing.setCost(maintenance.getCost());
        existing.setObservations(maintenance.getObservations());

        return maintenanceRepository.save(existing);
    }

}