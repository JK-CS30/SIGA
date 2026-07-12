/* ===== FUNCIONES DE GESTIÓN DE MODALES ===== */

function toggleSidebar() {
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

// Modal de Creación
function abrirModalCrear() { 
    document.getElementById('modalRental').style.display = 'flex'; 
}
function cerrarModalCrear() { 
    document.getElementById('modalRental').style.display = 'none'; 
}

// Modal de Cierre (Liquidación)
function abrirModalCierre(id) {
    var form = document.getElementById('formCierre');
    form.action = '/rental/close/' + id;
    document.getElementById('modalCierre').style.display = 'flex';
}

function cerrarModalCierre() {
    document.getElementById('modalCierre').style.display = 'none';
    document.getElementById('formCierre').reset();
    document.getElementById('divResponsable').style.display = 'none';
    document.getElementById('inputResponsable').required = false;
}

// Lógica para mostrar/ocultar encargado según método de pago
function evaluarMetodoPago() {
    const metodo = document.getElementById('selectMetodoPago').value;
    const divResponsable = document.getElementById('divResponsable');
    const inputResponsable = document.getElementById('inputResponsable');

    if (metodo === 'COBRO_TERCERO') {
        divResponsable.style.display = 'flex';
        inputResponsable.required = true;
    } else {
        divResponsable.style.display = 'none';
        inputResponsable.required = false;
        inputResponsable.value = ''; 
    }
}

// Modal de Confirmación Financiera
function abrirModalConfirmacion(id, montoActual, tipo) {
    const modal = document.getElementById('modalConfirmacionFinanciera');
    const form = document.getElementById('formConfirmacionFinanciera');
    const titulo = document.getElementById('tituloModalConfirmacion');
    const inputMonto = document.getElementById('inputMontoFinal');
    
    inputMonto.value = parseFloat(montoActual).toFixed(2);
    document.getElementById('inputObservacionesAjuste').value = '';

    if (tipo === 'deposit') {
        titulo.innerText = "Verificar Depósito Bancario";
    } else if (tipo === 'third-party') {
        titulo.innerText = "Confirmar Recibo de Cobro (Tercero)";
    }

    form.action = "/confirm-deposit/" + id;
    modal.style.display = 'flex';
}

function cerrarModalConfirmacion() {
    document.getElementById('modalConfirmacionFinanciera').style.display = 'none';
}

// Modal de Detalle
function verDetalleRental(id) {
    fetch('/rental/detail/' + id)
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener detalle');
            return response.json();
        })
        .then(r => {
            document.getElementById('rentId').textContent = r.id; 
            document.getElementById('rentCliente').innerText = r.customerName || 'Sin registrar';
            document.getElementById('rentfecha').innerText = r.date ? r.date.split('-').reverse().join('-') : '-';
            
            // Equipo
            if (r.equipment) {
                let marca = r.equipment.brand ? ` (${r.equipment.brand})` : '';
                document.getElementById('rentEquipo').innerHTML = `${r.equipment.code}${marca}`;
            } else {
                document.getElementById('rentEquipo').innerText = 'Sin equipo asignado';
            }
            
            // Operador
            document.getElementById('rentOperador').innerText = r.operator ? r.operator.username : 'Sin asignar';
            
            // Valores
            document.getElementById('rentDescripcion').innerText = r.serviceDescription || 'Sin descripción';
            document.getElementById('rentObservaciones').innerText = r.observaciones || 'Sin observaciones';
            document.getElementById('rentMonto').innerText = '$' + parseFloat(r.totalAmount || 0).toFixed(2);

            document.getElementById('modalDetalle').style.display = 'flex';
        })
        .catch(error => console.error('Error:', error));
}

function cerrarModalDetalle() {
    document.getElementById('modalDetalle').style.display = 'none';
}

// Eliminación
function eliminarRental(id) {
    if (confirm("¿Seguro que deseas eliminar este alquiler?")) {
        fetch(`/rental/delete/${id}`, { method: "DELETE" })
            .then(response => {
                if (response.ok) location.reload();
                else alert("Error al eliminar");
            });
    }
}