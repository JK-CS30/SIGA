package com.integrador1.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate date;
    
    private double totalAmount;

    private String customerName; 

    private String customerRuc; // Almacenado internamente para auditoría, oculto en la web
    
    private String serviceDescription; 
    private String status;
    private String paymentMethod;
    private String collectionsResponsible;

    private String observaciones;

    @Column(nullable = true)
    private String workSheetUrl;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MyAppUser operator;

    /**
     * Centralización de la visualización del destino en reportes.
     */
    public String getDisplayDestination() {
        if (this.customerName != null && !this.customerName.isBlank()) {
            return this.customerName + " - " + (this.serviceDescription != null ? this.serviceDescription : "");
        }
        return this.serviceDescription != null ? this.serviceDescription : "Servicio Interno / Evento";
    }

    public String getFormatedFlujo() {
        if (this.observaciones == null) return "";
        
        // Si manejas un campo específico para el método/flujo, úsalo aquí. 
        // Suponiendo que lo obtienes de una variable o del inicio del texto:
        if (this.observaciones.contains("COBRO_TERCERO")) {
            return this.observaciones.replace("COBRO_TERCERO", "Por Cobrar");
        }
        if (this.observaciones.contains("DEPOSITO")) {
            return this.observaciones.replace("DEPOSITO", "Depósito Bancario");
        }
        
        return this.observaciones;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() { 
        return id; }
    public void setId(Long id) { 
        this.id = id; }

    public LocalDate getDate() { 
        return date; }
    public void setDate(LocalDate date) { 
        this.date = date; }

    public double getTotalAmount() { 
        return totalAmount; }
    public void setTotalAmount(double totalAmount) { 
        this.totalAmount = totalAmount; }

    public String getCustomerName() { 
        return customerName; }
    public void setCustomerName(String customerName) { 
        this.customerName = customerName; }

    public String getCustomerRuc() { 
        return customerRuc; }
    public void setCustomerRuc(String customerRuc) { 
        this.customerRuc = customerRuc; }

    public String getServiceDescription() { 
        return serviceDescription; }
    public void setServiceDescription(String serviceDescription) { 
        this.serviceDescription = serviceDescription; }

    public String getStatus() { 
        return status; }
    public void setStatus(String status) { 
        this.status = status; }

    public String getObservaciones() { 
        return observaciones; }
    public void setObservaciones(String observaciones) { 
        this.observaciones = observaciones; }

    public Equipment getEquipment() { 
        return equipment; }
    public void setEquipment(Equipment equipment) { 
        this.equipment = equipment; }

    public MyAppUser getOperator() { 
        return operator; }
    public void setOperator(MyAppUser operator) { 
        this.operator = operator; }

    public String getPartWorkUrl() {
        return workSheetUrl;
    }

    public void setPartWorkUrl(String partWorkUrl) {
        this.workSheetUrl = partWorkUrl;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCollectionsResponsible() {
        return collectionsResponsible;
    }

    public void setCollectionsResponsible(String collectionsResponsible) {
        this.collectionsResponsible = collectionsResponsible;
    }

    
}