function toggleSidebar(){
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

function eliminarMantenimiento(id){
    if(confirm("¿Seguro que deseas eliminar este mantenimiento definitivamente? El historial se perderá.")){
        fetch(`/maintenance/delete/${id}`, {
            method: "DELETE"
        })
        .then(response => {
            if(response.ok){
                location.reload(); // Recarga la tabla de manera limpia
            } else {
                alert("No se pudo eliminar el registro. Verifique que no esté enlazado a otras dependencias.");
            }
        })
        .catch(error => {
            console.error("Error en la petición de borrado:", error);
            alert("Ocurrió un error de red al intentar conectar con el servidor.");
        });
    }
}

function abrirModal(){
    document.getElementById("modalMaintenace").style.display = "flex";
}

function abrirModalCierre(id) {
    var form = document.getElementById('formCierre');
    form.action = '/maintenance/close/' + id;
    document.getElementById('modalCierre').style.display = 'flex';
}

function cerrarModalCierre() {
    document.getElementById('modalCierre').style.display = 'none';
    document.getElementById('divResponsable').style.display = 'flex';
    document.getElementById('inputResponsable').required = false;
    document.getElementById('formCierre').reset();
}

function cerrarModal(){
    // Limpia el formulario antes de cerrarlo para evitar basura si el usuario vuelve a abrirlo
    document.getElementById("formMaintenance").reset();
    document.getElementById("modalMaintenace").style.display = "none";
}

// Cerrar el modal automáticamente si el usuario hace clic fuera de la caja blanca
window.onclick = function(event) {
    let modal = document.getElementById("modalMaintenace");
    if (event.target === modal) {
        cerrarModal();
    }
}

function verDetalleMantenimiento(id) {
    // Realizamos la consulta al servidor para traer los datos específicos de ese ID
    fetch('/maintenance/detail/' + id)
        .then(response => {
            if (!response.ok) throw new Error('No se pudo obtener el detalle');
            return response.json();
        })
        .then(m => {
            // Mapeamos los datos recibidos en los elementos del modal
            document.getElementById('detId').innerText = m.id;
            document.getElementById('detEquipo').innerText = m.equipment ? m.equipment.code : 'N/A';
            document.getElementById('detTipo').innerText = m.type;
            
            // Formatear fechas de YYYY-MM-DD a DD-MM-YYYY
            document.getElementById('detIngreso').innerText = m.entryDate ? m.entryDate.split('-').reverse().join('-') : 'Planificado';
            document.getElementById('detSalida').innerText = m.exitDate ? m.exitDate.split('-').reverse().join('-') : 'En Taller / No finalizado';
            
            document.getElementById('detHorometro').innerText = m.usageIndicator ? m.usageIndicator + ' hrs' : '-';
            document.getElementById('detProximo').innerText = m.nextMaintenanceUsage ? m.nextMaintenanceUsage + ' hrs' : 'No programado';
            document.getElementById('detCosto').innerText = m.cost ? 'S/ ' + parseFloat(m.cost).toFixed(2) : 'Sin costo registrado';
            document.getElementById('detEstado').innerText = m.status;
            
            document.getElementById('detDescripcion').innerText = m.description || 'Sin descripción';
            document.getElementById('detObservaciones').innerText = m.observations || 'Sin observaciones finales registradas';

            // Mostramos el modal en la pantalla
            document.getElementById('modalDetalle').style.display = 'flex';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ocurrió un error al cargar los datos del mantenimiento.');
        });
}

function cerrarModalDetalle() {
    document.getElementById('modalDetalle').style.display = 'none';
}