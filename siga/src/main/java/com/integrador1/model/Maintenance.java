package com.integrador1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_date", nullable = false , updatable= false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    @Column(name = "exit_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate exitDate;
    
    private String type;

    @Column(length = 1000)
    private String description;
    private String status;

    @NotNull
    private  Double usageIndicator;

    @Column(name = "cost")
    private Double cost;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "next_maintenance_usage")
    private Double nextMaintenanceUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private MyAppUser tecnico;


    //CONSTRUCTOR

    public Maintenance() {
        this.entryDate = LocalDate.now();
        this.status = "En Taller";
    }

    //GETTERS AND SETTERS

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public LocalDate getEntryDate() {
        return entryDate;
    }


    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }


    public LocalDate getExitDate() {
        return exitDate;
    }


    public void setExitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Double getUsageIndicator() {
        return usageIndicator;
    }


    public void setUsageIndicator(Double usageIndicator) {
        this.usageIndicator = usageIndicator;
    }


    public Double getCost() {
        return cost;
    }


    public void setCost(Double cost) {
        this.cost = cost;
    }


    public String getObservations() {
        return observations;
    }


    public void setObservations(String observations) {
        this.observations = observations;
    }


    public Double getNextMaintenanceUsage() {
        return nextMaintenanceUsage;
    }


    public void setNextMaintenanceUsage(Double nextMaintenanceUsage) {
        this.nextMaintenanceUsage = nextMaintenanceUsage;
    }


    public Equipment getEquipment() {
        return equipment;
    }


    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }


    public MyAppUser getTecnico() {
        return tecnico;
    }


    public void setTecnico(MyAppUser tecnico) {
        this.tecnico = tecnico;
    }
 
}
