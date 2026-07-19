package com.integrador1.controller;

import com.integrador1.repository.RentalRepository;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final EquipmentService equipmentService;
    private final MaintenanceService maintenanceService;
    private final RentalService rentalService;
    private final RentalRepository rentalRepository;

    public DashboardController(
            EquipmentService equipmentService,
            MaintenanceService maintenanceService,
            RentalService rentalService, 
            RentalRepository rentalRepository) {

        this.equipmentService = equipmentService;
        this.maintenanceService = maintenanceService;
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // ==========================================
        // 📈 METRICAS Y KPIS (LÓGICA EXISTENTE)
        // ==========================================

        // Métrica de Equipos Totales
        long totalEquipments = equipmentService.getAllEquipment()
                .stream()
                .filter(e -> !"ELIMINADO".equals(e.getStatus())) 
                .count();
        model.addAttribute("totalEquipments", (int) totalEquipments);

        // Métrica de Alquileres Activos
        long activeRentals = rentalService.listRentals()
                .stream()
                .filter(r -> "ACTIVO".equals(r.getStatus()))
                .count();
        model.addAttribute("activeRentals", activeRentals);

        // Métrica de Mantenimientos Abiertos
        model.addAttribute(
                "openMaintenances",
                maintenanceService.listMaintenance()
                        .stream()
                        .filter(m -> "En Mantenimiento".equals(m.getStatus()))
                        .count());

        // Total de ingresos del mes actual calculados desde el servicio
        Double total_Amount = rentalService.getTotalIngresosMesActual();
        model.addAttribute("total_Amount", total_Amount);

        // Cálculo de Ocupación de Inventario
        double ocupiedEquipment = 0.0;
        if (totalEquipments > 0) {
            ocupiedEquipment = (activeRentals * 100.0) / totalEquipments;
        }
        model.addAttribute("ocupiedEquipment", Math.round(ocupiedEquipment));

        // Estado de página activa para el Sidebar
        model.addAttribute("activePage", "dashboard");


        // ==========================================
        // 📊 DATOS PARA GRÁFICOS (NUEVA LÓGICA)
        // ==========================================
        
        // Gráfico 1: Evolución de Ingresos Mensuales (Línea)
        double[] ingresosMeses = new double[12];
        List<Object[]> queryResults = rentalRepository.getIngresosMensualesData();
        for (Object[] row : queryResults) {
            int mesIndex = ((Number) row[0]).intValue() - 1;
            if (mesIndex >= 0 && mesIndex < 12) {
                ingresosMeses[mesIndex] = ((Number) row[1]).doubleValue();
            }
        }

        // Gráfico 2: Comparativa Bruto vs Neto (Barras Dobles)
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

        // Inyectar colecciones de datos para Chart.js
        model.addAttribute("ingresosMeses", ingresosMeses);
        model.addAttribute("datosBruto", brutosPorMes);
        model.addAttribute("datosNeto", netosPorMes);

        // 🚨 IMPORTANTE: Retorna el "dashboard" o "dashboard/index" según requiera tu estructura de carpetas
        return "dashboard";
    }
}