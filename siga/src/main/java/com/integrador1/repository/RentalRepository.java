package com.integrador1.repository;

import com.integrador1.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByStatus(String status);

    // Listar activos, finalizados y pendientes omitiendo los eliminados lógicamente
    List<Rental> findByStatusNotOrderByIdDesc(String status);

    // Filtrar por un estado específico (ACTIVO o FINALIZADO)
    List<Rental> findByStatusOrderByIdDesc(String status);

    // Filtrar por estados pendientes (DEPOSITO Y COBROS)
    @Query("SELECT r FROM Rental r WHERE r.status IN ('PENDIENTE_DEPOSITO', 'PENDIENTE_COBRO') ORDER BY r.id DESC")
    List<Rental> findPendientes();

    // Suma total del historico
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0.0) FROM Rental r WHERE r.status != :deletedStatus")
    Double sumTotal(@Param("deletedStatus") String deletedStatus);

    // Suma total del mes
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0.0) FROM Rental r " +
           "WHERE r.status != :deletedStatus " +
           "AND YEAR(r.date) = YEAR(CURRENT_DATE) " +
           "AND MONTH(r.date) = MONTH(CURRENT_DATE)")
    Double sumTotalMes(@Param("deletedStatus") String deletedStatus);

    // Suma total del dia
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0.0) FROM Rental r " +
            "WHERE r.status = :successStatus " +
            "AND r.date = :today")
    Double sumTotalDiaParam(@Param("today") LocalDate today, @Param("successStatus") String successStatus);

    // Busca alquileres cuya fecha sea anterior a la fecha límite y que su campo de imagen NO esté vacío
    @Query("SELECT r FROM Rental r WHERE r.date < :thresholdDate AND r.workSheetUrl IS NOT NULL")
    List<Rental> findExpiredServices(@Param("thresholdDate") LocalDate thresholdDate);

    // GRAFICOS ESTADISTICOS
    // Agrupa montos por mes del año actual usando funciones nativas de PostgreSQL
    @Query(value = "SELECT EXTRACT(MONTH FROM r.date) as mes, SUM(r.total_amount) as total " +
                   "FROM rental r " +
                   "WHERE r.status <> 'ELIMINADO' AND EXTRACT(YEAR FROM r.date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                   "GROUP BY EXTRACT(MONTH FROM r.date) " +
                   "ORDER BY mes", nativeQuery = true)
    List<Object[]> getIngresosMensualesData();

    // GRAFICOS NETO VS BRUTO
    @Query(value = "SELECT EXTRACT(MONTH FROM r.date) as mes, " +
                   "COALESCE(SUM(r.bruto_amount), 0.0) as bruto, " +
                   "COALESCE(SUM(r.total_amount), 0.0) as neto " +
                   "FROM rental r " +
                   "WHERE r.status <> 'ELIMINADO' " +
                   "GROUP BY EXTRACT(MONTH FROM r.date) " +
                   "ORDER BY mes", nativeQuery = true)
    List<Map<String, Object>> getComparativeBrutoNetoData();


    @Query(value = "SELECT EXTRACT(MONTH FROM r.date) as mes, " +
                   "       COALESCE(SUM(r.bruto_amount), 0.0) as bruto, " +
                   "       COALESCE(SUM(r.total_amount), 0.0) as neto " +
                   "FROM rental r " +
                   "WHERE r.status <> 'ELIMINADO' " +
                   "GROUP BY EXTRACT(MONTH FROM r.date) " +
                   "ORDER BY mes", nativeQuery = true)
    List<Object[]> findFinancialDataByMonth();
}