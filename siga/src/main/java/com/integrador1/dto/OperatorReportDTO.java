package com.integrador1.dto;

import com.integrador1.model.Rental;
import java.util.List;

public class OperatorReportDTO {
    private String operatorName;
    private long totalIniciados;
    private long totalFinalizados;
    private double totalMonto;
    private List<Rental> rentalHistory;

    public OperatorReportDTO(String operatorName, long totalIniciados, long totalFinalizados, double totalMonto, List<Rental> rentalHistory) {
        this.operatorName = operatorName;
        this.totalIniciados = totalIniciados;
        this.totalFinalizados = totalFinalizados;
        this.totalMonto = totalMonto;
        this.rentalHistory = rentalHistory;
    }

    // Getters
    public String getOperatorName() { return operatorName; }
    public long getTotalIniciados() { return totalIniciados; }
    public long getTotalFinalizados() { return totalFinalizados; }
    public double getTotalMonto() { return totalMonto; }
    public List<Rental> getRentalHistory() { return rentalHistory; }
}