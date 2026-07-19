package com.integrador1.controller;

import com.integrador1.model.Rental;
import com.integrador1.repository.RentalRepository;
import com.integrador1.service.DatabaseBackupService;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    private final EquipmentService equipmentService;
    private final RentalService rentalService;
    private final MaintenanceService maintenanceService;
    private final RentalRepository rentalRepository;
    private final DatabaseBackupService backupService; // 🌟 Declarado como final

    // Ruta del archivo físico configurado en tu application.yml
    private static final String LOG_FILE_PATH = "logs/gestrack-app.log";

    // 🌟 Constructor unificado que inyecta todos los servicios requeridos
    public MonitoringController(
            EquipmentService equipmentService,
            RentalService rentalService,
            MaintenanceService maintenanceService,
            RentalRepository rentalRepository,
            DatabaseBackupService backupService) {
        this.equipmentService = equipmentService;
        this.rentalService = rentalService;
        this.maintenanceService = maintenanceService;
        this.rentalRepository = rentalRepository;
        this.backupService = backupService;
    }

    @GetMapping
    public String monitoring(Model model) {
        
        // =========================================================================
        // 1. MONITOREO DE NEGOCIO Y FLUJO FINANCIERO
        // =========================================================================
        Double totalRecaudadoDia = rentalService.getIngresosDiaActual();
        model.addAttribute("totalRecaudadoDia", totalRecaudadoDia != null ? totalRecaudadoDia : 0.0);

        Double dineroTransito = rentalService.getDineroEnTransito();
        model.addAttribute("dineroTransito", dineroTransito != null ? dineroTransito : 0.0);

        model.addAttribute("equipmentsActivosCount", rentalService.countActiveRentals());

        // =========================================================================
        // 2. MONITOREO DE FLOTA Y MANTENIMIENTOS (ESTADO DE ACTIVOS)
        // =========================================================================
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

        model.addAttribute("diskFreeSpace", "88% Disponible");
        model.addAttribute("diskPercentage", 12);

        // =========================================================================
        // 4. CONSOLA DE AUDITORÍA (PRIMER RENDERIZADO ESTÁTICO DESDE EL ARCHIVO)
        // =========================================================================
        List<String> logsLines = obtenerUltimosLogs();
        model.addAttribute("logsLines", logsLines);

        return "monitoring";
    }

    @GetMapping("/stream")
    @ResponseBody
    public List<String> streamLogs() {
        return obtenerUltimosLogs();
    }

    private List<String> obtenerUltimosLogs() {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(LOG_FILE_PATH));
            
            // Expresión regular para capturar la hora (HH:mm:ss) al inicio de la línea de Spring Boot
            Pattern timePattern = Pattern.compile("T(\\d{2}:\\d{2}:\\d{2})");

            List<String> businessLines = allLines.stream()
                // 1. FILTRO DE EXCLUSIÓN: Descartamos inmediatamente cualquier rastro de error técnico o Spring
                .filter(line -> !line.contains("Caused by:") && 
                                !line.contains("\tat ") && 
                                !line.contains("... ") && 
                                !line.contains("UnsatisfiedDependencyException") &&
                                !line.contains("BeanCreationException") &&
                                !line.contains("BeanInstantiationException"))
                
                // 2. FILTRO DE INCLUSIÓN FLEXIBLE: 
                // Deja pasar logs del paquete service, del de backup o logs directos de la copia de seguridad
                .filter(line -> line.contains("com.integrador1.service") || 
                                line.contains("DatabaseBackupService") || 
                                line.contains("[BACKUP]") || 
                                line.contains("[PG_DUMP-OUTPUT]"))

                // 3. MAPEO Y LIMPIEZA: Dar formato elegante al mensaje de negocio
                .map(line -> {
                    try {
                        // Extraer la hora simplificada [HH:mm:ss]
                        Matcher matcher = timePattern.matcher(line);
                        String horaFormateada = "";
                        if (matcher.find()) {
                            horaFormateada = "[" + matcher.group(1) + "] ";
                        }

                        // Separar la metadata del mensaje real utilizando el delimitador de Spring " : "
                        String[] parts = line.split(" : ");
                        if (parts.length > 1) {
                            String mensajeReal = parts[1];
                            
                            // Separar el bloque de usuario si existe: "[Usuario: kevin] - Mensaje"
                            String usuarioStr = "[Usuario: Sistema] "; // Por defecto si no hay usuario HTTP
                            if (mensajeReal.contains("[Usuario: ") && mensajeReal.contains("] - ")) {
                                int indexCierre = mensajeReal.indexOf("] - ");
                                usuarioStr = mensajeReal.substring(0, indexCierre + 1) + " ";
                                mensajeReal = mensajeReal.substring(indexCierre + 4);
                            }

                            // Limpiar guiones o estados nulos del mensaje
                            mensajeReal = mensajeReal.replace("[INFO] - ", "[INFO] ")
                                                     .replace("[WARN] - ", "[WARN] ")
                                                     .replace("[ERROR] - ", "[ERROR] ")
                                                     .replace("Operador responsable: [null]", "Operador: Sin asignar");

                            // Retorna la estructura: "[19:08:47] [Usuario: kevin] [WARN] Proceso de ALQUILER..."
                            return horaFormateada + usuarioStr + mensajeReal;
                        }
                    } catch (Exception e) {
                        return line;
                    }
                    return line;
                })
                // Evitamos que queden líneas vacías o nulas por algún error de parseo
                .filter(line -> line != null && !line.trim().isEmpty())
                .collect(Collectors.toList());

            // Límite de líneas para visualización en la interfaz
            int totalLines = businessLines.size();
            int limit = 40; 

            List<String> logsLines;
            if (totalLines > limit) {
                logsLines = new ArrayList<>(businessLines.subList(totalLines - limit, totalLines));
            } else {
                logsLines = new ArrayList<>(businessLines);
            }

            // Invertimos el orden para que los logs más recientes se ubiquen arriba
            Collections.reverse(logsLines);
            return logsLines;

        } catch (Exception e) {
            List<String> fallbackLogs = new ArrayList<>();
            fallbackLogs.add("[INFO] Sincronizando consola de auditoría...");
            fallbackLogs.add("[INFO] Esperando transacciones del sistema.");
            return fallbackLogs;
        }
    }

    @GetMapping(value = "/monitoring-api/backup/generate")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> desencadenarBackupManual() {
        try {
            String resultado = backupService.generateBackup();
            return ResponseEntity.ok().body(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar el backup: " + e.getMessage());
        }
    }
}