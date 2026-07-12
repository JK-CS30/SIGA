function toggleSidebar(){
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

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

// Variable global para capturar el próximo código autogenerado al cargar la página
const codigoSiguienteSujeto = document.getElementById('code').value;

function abrirModal() {
    document.getElementById('modalTitle').innerText = "Registrar Equipo";
    document.getElementById('formEquipment').action = "/equipment/save";
    
    // Forzamos a que el valor quede vacío para que Spring lo interprete como null
    document.getElementById('equipmentId').value = ""; 
    
    document.getElementById('formEquipment').reset();
    document.getElementById('code').value = codigoSiguienteSujeto;
    document.getElementById('modalEquipo').style.display = "flex";
}

function cerrarModal() {
    document.getElementById('modalEquipo').style.display = "none";
}

function editarEquipo(btn) {
    document.getElementById('modalTitle').innerText = "Editar Equipo";
    
    const id = btn.getAttribute('data-id');
    document.getElementById('formEquipment').action = "/equipment/update/" + id;
    document.getElementById('equipmentId').value = id;
    
    // Mapeamos los datos del botón a los inputs
    document.getElementById('code').value = btn.getAttribute('data-code');
    document.getElementById('brand').value = btn.getAttribute('data-brand');
    document.getElementById('serialNumber').value = btn.getAttribute('data-serial');
    document.getElementById('year').value = btn.getAttribute('data-year');
    document.getElementById('usageIndicator').value = btn.getAttribute('data-usage');
    
    document.getElementById('modalEquipo').style.display = "block";
}

function eliminarEquipo(id) {
    if (confirm("¿Estás seguro de que deseas eliminar este equipo?")) {
        fetch("/equipment/delete/" + id, {
            method: "DELETE"
        }).then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                alert("Error al intentar eliminar el equipo.");
            }
        });
    }
}

