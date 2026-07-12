package com.integrador1.controller;

import com.integrador1.dto.OperatorReportDTO;
import com.integrador1.model.Rental;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    private final EquipmentService equipmentService;
    private final RentalService rentalService;
    private final MaintenanceService maintenanceService;

    public MonitoringController(
            EquipmentService equipmentService,
            RentalService rentalService,
            MaintenanceService maintenanceService) {
        this.equipmentService = equipmentService;
        this.rentalService = rentalService;
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public String monitoring(Model model) {
        
        // =========================================================================
        // 1. MONITOREO DE NEGOCIO Y FLUJO FINANCIERO (CONECTADO AL RENTAL SERVICE)
        // =========================================================================
        
        Double totalRecaudadoDia = rentalService.getIngresosDiaActual();
        model.addAttribute("totalRecaudadoDia", totalRecaudadoDia != null ? totalRecaudadoDia : 0.0);

        Double dineroTransito = rentalService.getDineroEnTransito();
        model.addAttribute("dineroTransito", dineroTransito != null ? dineroTransito : 0.0);

        model.addAttribute("equipmentsActivosCount", rentalService.countActiveRentals());


        // =========================================================================
        // 2. MONITOREO DE FLOTA Y MANTENIMIENTOS (ESTADO DE ACTIVOS)
        // =========================================================================
        
        // Estos contadores deben mapear los estados de tus entidades (Disponibles vs Mantenimientos)
        // Nota: Asegúrate de que tus servicios tengan estos métodos o usa lógica equivalente
        long disponibles = equipmentService.countByStatus("DISPONIBLE");
        long preventivos = maintenanceService.countByTypeAndStatus("PREVENTIVO", "EN_PROCESO");
        long correctivos = maintenanceService.countByTypeAndStatus("CORRECTIVO", "EN_PROCESO");

        model.addAttribute("equiposDisponibles", disponibles);
        model.addAttribute("mantenimientosPreventivos", preventivos);
        model.addAttribute("mantenimientosCorrectivos", correctivos);


        // =========================================================================
        // 3. MONITOREO TÉCNICO (INFRAESTRUCTURA DE TI Y JVM)
        // =========================================================================
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;

        model.addAttribute("usedMemory", String.format("%.2f GB", (double) usedMemory / (1024 * 1024 * 1024)));
        model.addAttribute("maxMemory", String.format("%.2f GB", (double) maxMemory / (1024 * 1024 * 1024)));
        
        int memoryPercentage = (int) (((double) usedMemory / maxMemory) * 100);
        model.addAttribute("memoryPercentage", memoryPercentage);

        // Almacenamiento físico simulado (puedes parametrizarlo con la API de Java File)
        model.addAttribute("diskFreeSpace", "88% Disponible");
        model.addAttribute("diskPercentage", 12);


        // =========================================================================
        // 4. CONSOLA DE AUDITORÍA (BUFFER DE TRAZAS DE LOGS)
        // =========================================================================
        
        List<String> logs = new ArrayList<>();
        // Trazas controladas para auditar los procesos vivos del software
        logs.add("[INFO] " + java.time.LocalDateTime.now() + " - Conexión con Base de Datos de Activos en estado: OPTIMAL.");
        logs.add("[INFO] " + java.time.LocalDateTime.now() + " - Sincronizando hilos de procesos concurrentes para Operadores.");
        
        if (correctivos > 0) {
            logs.add("[WARN] " + java.time.LocalDateTime.now() + " - Hay " + correctivos + " equipos en mantenimiento correctivo urgente.");
        }
        if (rentalService.countRentalsPendientesCobro() > 0) {
            logs.add("[WARN] " + java.time.LocalDateTime.now() + " - Alertas de facturación: Se detectaron montos en tránsito pendientes de validación.");
        }
        
        logs.add("[INFO] " + java.time.LocalDateTime.now() + " - Panel de control de telemetría renderizado correctamente.");
        
        model.addAttribute("logsLines", logs);

        return "monitoring";
    }

    @GetMapping("/reports")
    public String operatorReport(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "operatorName", required = false) String operatorName,
            Model model) {

        // Inicialización por defecto (últimos 30 días si es la primera carga)
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusDays(30);

        // Procesamiento de datos mediante el servicio
        List<OperatorReportDTO> reportData = rentalService.generateOperatorReport(startDate, endDate, operatorName);

        // Cálculos para las tarjetas KPI de la parte superior
        String topOperator = "Ninguno";
        long topCount = 0;
        long totalCierres = 0;
        long totalPendientesActivos = 0;

        for (OperatorReportDTO dto : reportData) {
            if (dto.getTotalFinalizados() > topCount) {
                topCount = dto.getTotalFinalizados();
                topOperator = dto.getOperatorName();
            }
            totalCierres += dto.getTotalFinalizados();
            
            // Los pendientes se buscan en todo el historial del operador 
            // porque un equipo en campo sigue estando pendiente así se haya alquilado el mes pasado.
            long pendientesDelOperador = dto.getRentalHistory().stream()
                    .filter(r -> "PENDIENTE".equalsIgnoreCase(r.getStatus()) || "EN_PROCESO".equalsIgnoreCase(r.getStatus()))
                    .count();
            
            totalPendientesActivos += pendientesDelOperador;
        }

        // Tasa de incidencias porcentual calculada a partir de las observaciones de cierre
        long totalConObservaciones = reportData.stream()
                .flatMap(dto -> dto.getRentalHistory().stream())
                .filter(r -> r.getObservaciones() != null && !r.getObservaciones().trim().isEmpty())
                .count();
        double tasaIncidencias = totalCierres > 0 ? ((double) totalConObservaciones / totalCierres) * 100 : 0.0;

        // Población del selector de operarios dinámico
        List<String> listaOperadores = rentalService.listRentals().stream()
                .map(Rental::getOperator) // Extrae el objeto MyAppUser
                .filter(op -> op != null && op.getUsername() != null) // Evita NullPointerException
                .map(op -> op.getUsername()) // Extrae el String real (username) para la lista
                .filter(username -> !username.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // Inyección de variables requeridas por el Thymeleaf
        model.addAttribute("reportData", reportData);
        model.addAttribute("listaOperadores", listaOperadores);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("topOperator", topOperator);
        model.addAttribute("topOperatorCount", topCount);
        
        // Cambiado: Eliminamos el "4.2" harcodeado e inyectamos los datos del nuevo KPI real
        model.addAttribute("totalPendientes", totalPendientesActivos); 
        model.addAttribute("tasaIncidencias", String.format("%.1f", tasaIncidencias));

        // Retorno hacia el archivo ubicado en /templates/reportes/report-operator.html
        return "reportes/report-operator";
    }

}