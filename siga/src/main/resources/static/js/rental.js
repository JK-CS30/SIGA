// ==========================================
// ACCIONES DE LA INTERFAZ / SIDEBAR
// ==========================================
function toggleSidebar() {
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

// ==========================================
// MODAL DE CREACIÓN
// ==========================================
function abrirModalCrear() { 
    document.getElementById('modalRental').style.display = 'flex'; 
}
function cerrarModalCrear() { 
    document.getElementById('modalRental').style.display = 'none'; 
}

// ==========================================
// MODAL DE CIERRE (LIQUIDACIÓN)
// ==========================================
function abrirModalCierre(id, montoBase = null) {
    var form = document.getElementById('formCierre');
    form.action = '/rental/close/' + id;
    
    var inputMonto = document.getElementById('inputMontoTotal');
    if (inputMonto) {
        
        if (montoBase !== null && montoBase !== undefined) {
            inputMonto.value = parseFloat(montoBase).toFixed(2);
        } else {
            inputMonto.value = ''; 
        }
        
        // Escuchar cambios por si el usuario escribe manualmente
        inputMonto.addEventListener('input', calcularImpuestos);
    }
    
    // Limpiamos el checkbox para que empiece desmarcado
    var chkFacturado = document.getElementById('chkFacturado');
    if (chkFacturado) {
        chkFacturado.checked = false;
    }
    
    document.getElementById('modalCierre').style.display = 'flex';
    calcularImpuestos(); // Oculta el desglose inicialmente
}

// Lógica para calcular subtotal, IGV y Total Neto en tiempo real
function calcularImpuestos() {
    const chkFacturado = document.getElementById('chkFacturado');
    const divDesglose = document.getElementById('divDesglose');
    const inputMonto = document.getElementById('inputMontoTotal');
    
    if (!inputMonto || !divDesglose) return;

    let monto = parseFloat(inputMonto.value) || 0;

    if (chkFacturado && chkFacturado.checked) {
        let subtotal = monto;
        let igv = monto * 0.18;
        let totalFinal = subtotal + igv;

        document.getElementById('txtSubtotal').innerText = `S/ ${subtotal.toFixed(2)}`;
        document.getElementById('txtIGV').innerText = `S/ ${igv.toFixed(2)}`;
        document.getElementById('txtTotalFinal').innerText = `S/ ${totalFinal.toFixed(2)}`;
        
        divDesglose.style.display = 'block';
    } else {
        divDesglose.style.display = 'none';
    }
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

// ==========================================
// MODAL DE CONFIRMACIÓN FINANCIERA
// ==========================================
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

// ==========================================
// MODAL DE DETALLE (FETCH API)
// ==========================================
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
            
            // Corregido: Unificado a Soles Peruanos (S/) en lugar de símbolo de Dólar ($)
            document.getElementById('rentMonto').innerText = 'S/ ' + parseFloat(r.brutoAmount || 0).toFixed(2);

            document.getElementById('modalDetalle').style.display = 'flex';
        })
        .catch(error => console.error('Error:', error));
}

function cerrarModalDetalle() {
    document.getElementById('modalDetalle').style.display = 'none';
}

// ==========================================
// ACCIONES DE ELIMINACIÓN
// ==========================================
function eliminarRental(id) {
    if (confirm("¿Seguro que deseas eliminar este alquiler?")) {
        fetch(`/rental/delete/${id}`, { method: "DELETE" })
            .then(response => {
                if (response.ok) location.reload();
                else alert("Error al eliminar");
            });
    }
}