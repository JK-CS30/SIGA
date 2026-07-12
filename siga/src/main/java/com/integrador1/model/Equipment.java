package com.integrador1.model;

import jakarta.persistence.*;
import java.util.Base64;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Transient;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;
    @NotBlank
    private String brand;
    @NotBlank
    private String serialNumber;
    private Integer year;
    @NotNull
    private Double usageIndicator;
    @Column(name = "imagen", columnDefinition = "bytea")
    @org.hibernate.annotations.JdbcType(org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType.class)
    private byte[] imagen;
    @Column(name = "status", nullable = false)
    private String status = "DISPONIBLE"; // Estado inicial por defecto

    
    // GETTERS Y SETTERS 


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public byte[] getImagen() {
        return imagen;
    }
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
    public Double getUsageIndicator() {
        return usageIndicator;
    }
    public void setUsageIndicator(Double usageIndicator) {
        this.usageIndicator = usageIndicator;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    } 
    
}
