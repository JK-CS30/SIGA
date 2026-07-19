package com.integrador1.service;

import com.integrador1.dto.OperatorReportDTO;
import com.integrador1.model.Equipment;
import com.integrador1.model.Maintenance;
import com.integrador1.model.Rental;
import com.integrador1.repository.EquipmentRepository;
import com.integrador1.repository.RentalRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalService.class);
    
    private final RentalRepository rentalRepository;
    // Inyeccion del repositorio de equipos
    private final EquipmentRepository equipmentRepository;

    public RentalService(RentalRepository rentalRepository, EquipmentRepository equipmentRepository) {
        this.rentalRepository = rentalRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public Optional<Rental> findById(Long id){
        return rentalRepository.findById(id);
    }

    public void save(Rental rental) {
        rentalRepository.save(rental);
    }

    public List<Rental> listByStatus(String status) {
        return rentalRepository.findByStatusOrderByIdDesc(status);
    }

    public List<Rental> listPendientes() {
        return rentalRepository.findPendientes();
    }

    @Transactional
    public Rental registerRental(Rental rental) {
        if (rental.getDate() == null) {
            rental.setDate(LocalDate.now());
        }
        rental.setStatus("ACTIVO");
        rental.setTotalAmount(0.0);

        String equipmentCode = "N/A";
        if (rental.getEquipment() != null && rental.getEquipment().getId() != null) {
            Equipment equipment = equipmentRepository.findById(rental.getEquipment().getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            equipmentCode = equipment.getCode();
            equipment.setStatus("ALQUILADO"); 
            equipmentRepository.save(equipment);
        }

        Rental savedRental = rentalRepository.save(rental);
        
        // Traba de log estructurada para creación
        log.info("[INFO] - Proceso de ALQUILER iniciado exitosamente. ID: [{}]. Equipo Asignado: [{}]. Operador responsable: [{}].", 
                savedRental.getId(), equipmentCode, 
                (savedRental.getOperator() != null ? savedRental.getOperator().getUsername() : "ANÓNIMO"));
        
        return savedRental;
    }

    public List<Rental> listRentals() {
        return rentalRepository.findByStatusNotOrderByIdDesc("ELIMINADO");
    }

    public List<Rental> listDeletedRentals() {
        return rentalRepository.findByStatusOrderByIdDesc("ELIMINADO");
    }

    public Rental getRental(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
    }

    @Transactional
    public void deleteRental(Long id) {
        Rental rental = getRental(id);
        rental.setStatus("ELIMINADO"); // Borrado lógico corporativo

        String equipmentCode = "N/A";
        if (rental.getEquipment() != null) {
            Equipment equipment = rental.getEquipment();
            equipmentCode = equipment.getCode();
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment);
        }

        rentalRepository.save(rental);
        log.warn("[WARN] - Proceso de ALQUILER ID [{}] ha sido ELIMINADO del sistema. El equipo [{}] volvió a estado DISPONIBLE.", 
                id, equipmentCode);
    }

    @Transactional
    public void closeRental(Long id, double totalAmount, double brutoAmount, boolean facturado, String paymentMethod, String collectionsResponsible, String observaciones) {
        Rental rental = getRental(id);
        
        rental.setTotalAmount(totalAmount);     // Monto Neto
        rental.setBrutoAmount(brutoAmount);     // Monto Bruto con IGV
        rental.setFacturado(facturado);         // Flag de factura (true/false)
        
        rental.setPaymentMethod(paymentMethod);
        rental.setCollectionsResponsible("COBRO_TERCERO".equalsIgnoreCase(paymentMethod) ? collectionsResponsible : null);
        rental.setObservaciones(observaciones);
        
        // Manejo de estados de flujo de caja
        if ("EFECTIVO".equalsIgnoreCase(paymentMethod)) {
            rental.setStatus("FINALIZADO");
        } else if ("DEPOSITO".equalsIgnoreCase(paymentMethod)) {
            rental.setStatus("PENDIENTE_DEPOSITO");
        } else if ("COBRO_TERCERO".equalsIgnoreCase(paymentMethod)) {
            rental.setStatus("PENDIENTE_COBRO");
        }

        // Liberación del equipo asignado
        if (rental.getEquipment() != null && rental.getEquipment().getId() != null) {
            Equipment equipment = equipmentRepository.findById(rental.getEquipment().getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado para liberación"));
            equipment.setStatus("DISPONIBLE");
            equipmentRepository.save(equipment); 
        }

        rentalRepository.save(rental);

        // Log detallado de liquidación financiera
        log.info("[INFO] - ALQUILER ID [{}] LIQUIDADO. Método de Pago: [{}]. Estado de Caja resultante: [{}]. Monto Neto: [S/. {}].", 
                id, paymentMethod, rental.getStatus(), totalAmount);
    }

    @Transactional
    public void confirmDeposit(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_DEPOSITO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones() );
            rentalRepository.save(rental);
        }

        log.info("[INFO] - PAGO CONFIRMADO: Depósito bancario verificado para el ALQUILER ID [{}]. Caja cerrada como FINALIZADO.", id);
    }


    @Transactional
    public void confirmThirdPartyCollection(Long id) {
        Rental rental = getRental(id);
        if ("PENDIENTE_COBRO".equals(rental.getStatus())) {
            rental.setStatus("FINALIZADO");
            rental.setObservaciones(rental.getObservaciones());
            rentalRepository.save(rental);
        }

        log.info("[INFO] - PAGO CONFIRMADO: Cobro por tercero validado para el ALQUILER ID [{}]. Responsable de recaudación externo liberado.", id);
    }

    public Rental updateRental(Long id, Rental rental) {
        Rental existingRental = getRental(id);
        existingRental.setCustomerName(rental.getCustomerName());
        existingRental.setServiceDescription(rental.getServiceDescription());
        existingRental.setEquipment(rental.getEquipment());
        existingRental.setOperator(rental.getOperator());
        existingRental.setTotalAmount(rental.getTotalAmount());
        existingRental.setStatus(rental.getStatus());
        existingRental.setObservaciones(rental.getObservaciones());
        
        Rental updatedRental = rentalRepository.save(existingRental);

        log.info("[INFO] - Datos del ALQUILER ID [{}] modificados en el sistema por el personal de administración.", id);
        return updatedRental;
    }
    // CALCULO SUMA TOTAL POR DIA, MES E HISTORICO 

    //suma total por dia STATUS: Finalizado 
    public Double getIngresosDiaActual() {
        return rentalRepository.sumTotalDiaParam(LocalDate.now(), "FINALIZADO");
    }

    //suma total STATUS: en transito(pendiente de deposito/cobro)
    public Double getDineroEnTransito() {
        List<Rental> pendientes = rentalRepository.findPendientes();
        return pendientes.stream()
                .mapToDouble(rental -> rental.getTotalAmount())
                .sum();
    }

    // Generacion estructura de datos por operador con filtros
    public List<OperatorReportDTO> generateOperatorReport(LocalDate start, LocalDate end, String filterOperator) {
        // 1. Traemos absolutamente todos los alquileres del sistema
        List<Rental> allRentals = rentalRepository.findByStatusNotOrderByIdDesc("ELIMINADO");

        // 2. Agrupamos primero por operador (usando el username del objeto complejo MyAppUser)
        return allRentals.stream()
            .filter(r -> r.getOperator() != null && r.getOperator().getUsername() != null)
            // Si el usuario seleccionó un operador específico en el combo, filtramos aquí
            .filter(r -> (filterOperator == null || filterOperator.trim().isEmpty() || 
                    r.getOperator().getUsername().equalsIgnoreCase(filterOperator)))
            .collect(Collectors.groupingBy(r -> r.getOperator().getUsername()))
            .entrySet().stream()
            .map(entry -> {
                String operatorName = entry.getKey();
                List<Rental> operatorHistory = entry.getValue();

                // METRICA 1: Alquileres iniciados en el rango de fechas seleccionado
                long iniciados = operatorHistory.stream()
                    .filter(r -> (start == null || !r.getDate().isBefore(start)))
                    .filter(r -> (end == null || !r.getDate().isAfter(end)))
                    .count();

                // METRICA 2: Alquileres finalizados en el rango de fechas seleccionado
                long finalizados = operatorHistory.stream()
                    .filter(r -> "FINALIZADO".equalsIgnoreCase(r.getStatus()))
                    .filter(r -> (start == null || !r.getDate().isBefore(start)))
                    .filter(r -> (end == null || !r.getDate().isAfter(end)))
                    .count();

                // METRICA 3: Monto movilizado en el rango de fechas seleccionado
                double totalMonto = operatorHistory.stream()
                    .filter(r -> (start == null || !r.getDate().isBefore(start)))
                    .filter(r -> (end == null || !r.getDate().isAfter(end)))
                    .mapToDouble(Rental::getTotalAmount)
                    .sum();

                // GUARDAMOS EL HISTORIAL COMPLETO: Es crucial para poder auditar los PENDIENTES reales 
                // sin importar si se iniciaron hace 2 meses.
                return new OperatorReportDTO(operatorName, iniciados, finalizados, totalMonto, operatorHistory);
            })
            // Opcional: Ocultar operadores que tengan 0 movimientos en el rango de fechas para limpiar la vista
            .filter(dto -> dto.getTotalIniciados() > 0 || dto.getTotalFinalizados() > 0)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> obtenerDataGraficoFinanciero() {
        return rentalRepository.getComparativeBrutoNetoData();
    }

    //conteo de alquileres pendientes de deposito/cobro
    public long countRentalsPendientesCobro() {
        return rentalRepository.findPendientes().size();
    }

    //suma total por mes 
    public Double getTotalIngresosMesActual() {
        return rentalRepository.sumTotalMes("ELIMINADO");
    }

    public long countActiveRentals() {
        return rentalRepository.countByStatus("ACTIVO");
    }
    
}