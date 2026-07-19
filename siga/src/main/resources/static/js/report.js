/**
 * SIGA - Módulo de Reportes y Analítica Avanzada
 * Arquitectura de Frontend Senior (Chart.js + DOM Interactivo)
 */

document.addEventListener("DOMContentLoaded", () => {
    // Inicialización limpia y centralizada de todos los componentes gráficos
    initIncomeChart();
    initProductivityChart();
    initMaintenanceChart();
    initBrutoVsNetoChart(); 
});

// =========================================================================
// 📊 1. GRÁFICO: EVOLUCIÓN DE INGRESOS MENSUALES (LÍNEAS)
// =========================================================================
function initIncomeChart() {
    const canvas = document.getElementById('chartIngresosMensuales');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const dataBackend = window.ingresosMesesData || [];

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
            datasets: [{
                label: 'Ingresos Mensuales (S/)',
                data: dataBackend,
                borderColor: '#14b8a6',
                backgroundColor: 'rgba(20, 184, 166, 0.06)',
                borderWidth: 3,
                fill: true,
                tension: 0.35,
                pointBackgroundColor: '#14b8a6',
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#64748b' } },
                x: { grid: { display: false }, ticks: { color: '#64748b' } }
            }
        }
    });
}

// =========================================================================
// 📊 2. GRÁFICO: PRODUCTIVIDAD POR OPERADOR (BARRAS HORIZONTALES)
// =========================================================================
function initProductivityChart() {
    const canvas = document.getElementById('chartProductividad');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const labelsBackend = window.labelsOperadoresData || [];
    const montosBackend = window.montosOperadoresData || [];

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labelsBackend,
            datasets: [{
                label: 'Soles Movilizados',
                data: montosBackend,
                backgroundColor: '#38bdf8',
                borderRadius: 6,
                maxBarThickness: 25
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#64748b' } },
                y: { grid: { display: false }, ticks: { color: '#94a3b8' } }
            }
        }
    });
}

// =========================================================================
// 📊 3. GRÁFICO: GASTO EN MANTENIMIENTO (BARRAS VERTICALES)
// =========================================================================
function initMaintenanceChart() {
    const canvas = document.getElementById('chartGastoMantenimiento');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const labelsBackend = window.labelsEquiposMantData || [];
    const costosBackend = window.costosEquiposMantData || [];

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labelsBackend,
            datasets: [{
                label: 'Gasto Acumulado (S/)',
                data: costosBackend,
                backgroundColor: '#facc15',
                borderRadius: 6,
                maxBarThickness: 35
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#64748b' } },
                x: { grid: { display: false }, ticks: { color: '#94a3b8' } }
            }
        }
    });
}

// =========================================================================
// 📊 🆕 4. GRÁFICO: BRUTO VS NETO COMPARATIVO (BARRAS DOBLES)
// =========================================================================
function initBrutoVsNetoChart() {
    const canvas = document.getElementById('chartBrutoVsNeto');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const datosBruto = window.datosBrutoData || [];
    const datosNeto = window.datosNetoData || [];

    // Auditoría rápida en la consola del navegador para certificar los arreglos de 12 meses
    console.log("--- 📊 Datos Cargados en Gráfico Financiero ---");
    console.log("Bruto: ", datosBruto);
    console.log("Neto: ", datosNeto);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
            datasets: [
                {
                    label: 'Ingreso Bruto',
                    data: datosBruto,
                    backgroundColor: '#38bdf8',
                    borderRadius: 4,
                    maxBarThickness: 15
                },
                {
                    label: 'Ingreso Neto',
                    data: datosNeto,
                    backgroundColor: '#14b8a6',
                    borderRadius: 4,
                    maxBarThickness: 15
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    labels: { color: '#94a3b8', boxWidth: 12, font: { size: 11 } }
                }
            },
            scales: {
                y: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#64748b' } },
                x: { grid: { display: false }, ticks: { color: '#64748b' } }
            }
        }
    });
}

// =========================================================================
// 📋 5. MECANISMO INTERACTIVO: FILAS EXPANDIBLES (TREE GRID) & SIDEBAR
// =========================================================================
function toggleSidebar() {
    document.body.classList.toggle('sidebar-collapsed');
}

function toggleDetails(index) {
    const detailRow = document.getElementById('detail-' + index);
    const icon = document.getElementById('icon-' + index);
    
    if (!detailRow || !icon) return;

    if (detailRow.classList.contains('show')) {
        detailRow.classList.remove('show');
        icon.className = 'fa-solid fa-chevron-down';
    } else {
        detailRow.classList.add('show');
        icon.className = 'fa-solid fa-chevron-up';
    }
}