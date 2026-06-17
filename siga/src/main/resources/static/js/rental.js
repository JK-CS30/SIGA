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

function abrirModalCrear() { document.getElementById('modalRental').style.display = 'block'; }
function cerrarModalCrear() { document.getElementById('modalRental').style.display = 'none'; }

function abrirModalCierre(id) {
    var form = document.getElementById('formCierre');
    form.action = '/rental/close/' + id;
    document.getElementById('modalCierre').style.display = 'block';
}

function evaluarMetodoPago() {
    var metodo = document.getElementById('selectMetodoPago').value;
    var divResponsable = document.getElementById('divResponsable');
    var inputResponsable = document.getElementById('inputResponsable');

    if (metodo === 'COBRO_TERCERO') {
        divResponsable.style.display = 'block';
        inputResponsable.required = true;
    } else {
        divResponsable.style.display = 'none';
        inputResponsable.required = false;
        inputResponsable.value = '';
    }
}

function cerrarModalCierre() {
    document.getElementById('modalCierre').style.display = 'none';
    document.getElementById('divResponsable').style.display = 'none';
    document.getElementById('inputResponsable').required = false;
    document.getElementById('formCierre').reset();
}