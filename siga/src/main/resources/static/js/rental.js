function eliminarRental(id) {

    if (confirm("¿Seguro que deseas eliminar este alquiler?")) {

        fetch(`/rental/delete/${id}`, {
            method: "DELETE"
        })
            .then(response => {

                if (response.ok) {
                    location.reload();
                } else {
                    alert("Error al eliminar");
                }

            });

    }
}

/* ===== MODAL ===== */

function abrirModalCrear() { document.getElementById('modalRental').style.display = 'flex'; }
function cerrarModalCrear() { document.getElementById('modalRental').style.display = 'none'; }

function abrirModalCierre(id) {
    var form = document.getElementById('formCierre');
    form.action = '/rental/close/' + id;
    document.getElementById('modalCierre').style.display = 'flex';
}

function evaluarMetodoPago() {
    var metodo = document.getElementById('selectMetodoPago').value;
    var divResponsable = document.getElementById('divResponsable');
    var inputResponsable = document.getElementById('inputResponsable');

    if (metodo === 'COBRO_TERCERO') {
        divResponsable.style.display = 'flex';
        inputResponsable.required = true;
    } else {
        divResponsable.style.display = 'flex';
        inputResponsable.required = false;
        inputResponsable.value = '';
    }
}

function cerrarModalCierre() {
    document.getElementById('modalCierre').style.display = 'none';
    document.getElementById('divResponsable').style.display = 'flex';
    document.getElementById('inputResponsable').required = false;
    document.getElementById('formCierre').reset();
}

function abrirModalConfirmacion(id, montoActual, tipo) {
    const modal = document.getElementById('modalConfirmacionFinanciera');
    const form = document.getElementById('formConfirmacionFinanciera');
    const titulo = document.getElementById('tituloModalConfirmacion');
    const inputMonto = document.getElementById('inputMontoFinal');
    
    // Inyectamos el monto que estaba registrado originalmente
    inputMonto.value = montoActual.toFixed(2);
    document.getElementById('inputObservacionesAjuste').value = '';

    // Ajustamos dinámicamente el título y el endpoint de Spring Boot según el flujo
    if (tipo === 'deposit') {
        titulo.innerText = "Verificar Depósito Bancario";
        form.action = "/rental/confirm-deposit/" + id;
    } else if (tipo === 'third-party') {
        titulo.innerText = "Confirmar Recibo de Cobro (Tercero)";
        form.action = "/rental/confirm-third-party/" + id;
    }

    modal.style.display = 'flex';
}

function cerrarModalConfirmacion() {
    document.getElementById('modalConfirmacionFinanciera').style.display = 'none';
}

function descargarExcelRendicion() {
    const form = document.getElementById('formRendicion');
    
    if (form.checkValidity()) {
        const formData = new FormData(form);
        const params = new URLSearchParams(formData).toString();
        // CORRECCIÓN: Agrega la barra '/' antes de rental
        window.location.href = "/rendicion?" + params;
    } else {
        alert("Por favor, seleccione un operador y el rango de fechas para la rendición.");
    }
}