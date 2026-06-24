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