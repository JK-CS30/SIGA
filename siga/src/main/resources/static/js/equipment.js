
function editarEquipo(btn) {

    document.getElementById("equipmentId").value =
        btn.dataset.id;

    document.getElementById("brand").value =
        btn.dataset.brand;

    document.getElementById("serialNumber").value =
        btn.dataset.serial;

    document.getElementById("year").value =
        btn.dataset.year;

    document.getElementById("hourMeter").value =
        btn.dataset.hour;

    document.getElementById("mileage").value =
        btn.dataset.mileage;

    document.getElementById("formEquipment").action =
        `/equipment/update/${btn.dataset.id}`;

    abrirModal();
}

function filtrarTabla() {
    let input = document.getElementById("search").value.toLowerCase();
    let filas = document.querySelectorAll("tbody tr");

    filas.forEach(fila => {
        let texto = fila.innerText.toLowerCase();
        fila.style.display = texto.includes(input) ? "" : "none";
    });
}

function eliminarEquipo(id) {

    if (confirm("¿Seguro que deseas eliminar este equipo?")) {

        fetch(`/equipment/delete/${id}`, {
            method: "DELETE"
        })
            .then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
    }
}


/* ===== MODAL ===== */

function abrirModal() {
    document.getElementById("modalEquipo").style.display = "flex";
}

function cerrarModal() {
    document.getElementById("modalEquipo").style.display = "none";
}

window.onclick = function(event) {
    let modal = document.getElementById("modalEquipo");

    if (event.target === modal) {
        cerrarModal();
    }
}

