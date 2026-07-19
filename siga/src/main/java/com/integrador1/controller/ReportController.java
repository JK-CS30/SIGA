package com.integrador1.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.integrador1.dto.OperatorReportDTO;
import com.integrador1.model.Rental;
import com.integrador1.repository.RentalRepository;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;

@Controller 
@RequestMapping("/reports")
public class ReportController {

    private final EquipmentService equipmentService;
    private final RentalService rentalService;
    private final MaintenanceService maintenanceService;
    private final RentalRepository rentalRepository;

    public ReportController(
            EquipmentService equipmentService,
            RentalService rentalService,
            MaintenanceService maintenanceService,
            RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
        this.equipmentService = equipmentService;
        this.rentalService = rentalService;
        this.maintenanceService = maintenanceService;
    }

    @GetMapping("/grafic")
    public String operatorReport(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "operatorName", required = false) String operatorName,
            Model model) {

        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusDays(30);

        List<OperatorReportDTO> reportData = rentalService.generateOperatorReport(startDate, endDate, operatorName);

        // ==========================================
        // 📊 PROCESAMIENTO DE DATOS PARA GRÁFICOS
        // ==========================================
        
        // Gráfico 1: Productividad por Operador
        List<String> labelsOperadores = reportData.stream().map(OperatorReportDTO::getOperatorName).collect(Collectors.toList());
        List<Double> montosOperadores = reportData.stream().map(OperatorReportDTO::getTotalMonto).collect(Collectors.toList());

        // Gráfico 2: Evolución Financiera Mensual
        double[] ingresosMeses = new double[12];
        List<Object[]> queryResults = rentalRepository.getIngresosMensualesData();
        for (Object[] row : queryResults) {
            int mesIndex = ((Number) row[0]).intValue() - 1;
            double total = ((Number) row[1]).doubleValue();
            ingresosMeses[mesIndex] = total;
        }

        // Gráfico 3: Top de Gastos de Mantenimiento
        List<com.integrador1.model.Maintenance> topMaintenances = maintenanceService.listMaintenance().stream()
                .filter(m -> m.getEquipment() != null && m.getCost() > 0)
                .sorted((m1, m2) -> Double.compare(m2.getCost(), m1.getCost()))
                .limit(5)
                .collect(Collectors.toList());
        
        List<String> labelsEquiposMant = topMaintenances.stream().map(m -> m.getEquipment().getCode()).collect(Collectors.toList());
        List<Double> costosEquiposMant = topMaintenances.stream().map(m -> m.getCost()).collect(Collectors.toList());

        // 🆕 GRÁFICO 4: Comparativa Anual Ingreso Bruto vs Ingreso Neto Unificado
        List<Map<String, Object>> dataGrafico = rentalService.obtenerDataGraficoFinanciero();
        double[] brutosPorMes = new double[12];
        double[] netosPorMes = new double[12];
        
        for (Map<String, Object> row : dataGrafico) {
            if (row.get("mes") != null) {
                int mes = ((Number) row.get("mes")).intValue() - 1; 
                if (mes >= 0 && mes < 12) {
                    brutosPorMes[mes] = row.get("bruto") != null ? ((Number) row.get("bruto")).doubleValue() : 0.0;
                    netosPorMes[mes] = row.get("neto") != null ? ((Number) row.get("neto")).doubleValue() : 0.0;
                }
            }
        }

        // ==========================================
        // 📈 CÁLCULOS DE KPIS Y POBLACIÓN
        // ==========================================
        String topOperator = "Ninguno"; long topCount = 0; long totalCierres = 0; long totalPendientesActivos = 0;
        for (OperatorReportDTO dto : reportData) {
            if (dto.getTotalFinalizados() > topCount) { topCount = dto.getTotalFinalizados(); topOperator = dto.getOperatorName(); }
            totalCierres += dto.getTotalFinalizados();
            long pendientes = dto.getRentalHistory().stream().filter(r -> "PENDIENTE".equalsIgnoreCase(r.getStatus()) || "EN_PROCESO".equalsIgnoreCase(r.getStatus())).count();
            totalPendientesActivos += pendientes;
        }
        long totalConObservaciones = reportData.stream().flatMap(dto -> dto.getRentalHistory().stream()).filter(r -> r.getObservaciones() != null && !r.getObservaciones().trim().isEmpty()).count();
        double tasaIncidencias = totalCierres > 0 ? ((double) totalConObservaciones / totalCierres) * 100 : 0.0;
        List<String> listaOperadores = rentalService.listRentals().stream().map(Rental::getOperator).filter(op -> op != null && op.getUsername() != null).map(op -> op.getUsername()).distinct().collect(Collectors.toList());

        // Inyección al Modelo
        model.addAttribute("reportData", reportData);
        model.addAttribute("listaOperadores", listaOperadores);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("topOperator", topOperator);
        model.addAttribute("topOperatorCount", topCount);
        model.addAttribute("totalPendientes", totalPendientesActivos);
        model.addAttribute("tasaIncidencias", String.format("%.1f", tasaIncidencias));

        // Arreglos de gráficos
        model.addAttribute("labelsOperadores", labelsOperadores);
        model.addAttribute("montosOperadores", montosOperadores);
        model.addAttribute("ingresosMeses", ingresosMeses);
        model.addAttribute("labelsEquiposMant", labelsEquiposMant);
        model.addAttribute("costosEquiposMant", costosEquiposMant);
        model.addAttribute("datosBruto", brutosPorMes);
        model.addAttribute("datosNeto", netosPorMes);

        return "reportes/report-operator";
    }
}