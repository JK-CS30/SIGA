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

function abrirModal() {

    document.getElementById("modalRental").style.display = "flex";

}

function cerrarModal() {

    document.getElementById("modalRental").style.display = "none";

}

window.onclick = function(event) {

    let modal = document.getElementById("modalRental");

    if (event.target === modal) {
        cerrarModal();
    }

}