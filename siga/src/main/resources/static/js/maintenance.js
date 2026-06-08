function eliminarMantenimiento(id){

    if(confirm("¿Seguro que deseas eliminar este mantenimiento?")){

        fetch(`/maintenance/delete/${id}`,{
            method:"DELETE"
        })
            .then(response => {
                if(response.ok){
                    location.reload();
                }
            });
    }
}

function abrirModal(){
    document.getElementById("modalMaintenace")
        .style.display = "flex";
}

function cerrarModal(){
    document.getElementById("modalMaintenace")
        .style.display = "none";
}