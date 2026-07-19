function toggleSidebar() {
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

document.addEventListener("DOMContentLoaded", function () {
    const terminal = document.getElementById("terminalMonitor");
    const btnBackup = document.getElementById('btnBackup');

    // 🛑 Control de seguridad: Si no estamos en la página de monitoreo, no hace nada.
    if (!terminal) return; 

    // Guardamos la ruta base corregida
    const streamUrl = "/monitoring/stream"; 

    function actualizarLogs() {
        fetch(streamUrl, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
        .then(response => {
            if (response.status === 403) {
                throw new Error("403_FORBIDDEN");
            }
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(logs => {
            // Creamos un fragmento en memoria para evitar que la pantalla "parpadee" 
            const fragment = document.createDocumentFragment();

            logs.forEach(line => {
                const div = document.createElement("div");
                div.className = "log-line flex items-center py-2 border-b border-gray-800 text-sm";

                let restante = line;

                // 1. Extraer la Hora [HH:mm:ss]
                let hora = "";
                const timeMatch = restante.match(/^\[\d{2}:\d{2}:\d{2}\]/);
                if (timeMatch) {
                    hora = timeMatch[0];
                    restante = restante.substring(hora.length).trim();
                }

                // 2. Extraer el Usuario [Usuario: nombre]
                let usuario = "[Usuario: Sistema]";
                const userMatch = restante.match(/^\[Usuario:\s*([^\]]+)\]/);
                if (userMatch) {
                    usuario = `[User: ${userMatch[1]}]`;
                    restante = restante.substring(userMatch[0].length).trim();
                }

                // 3. Identificar el Tipo de Log ([INFO], [WARN], etc.)
                let badgeClass = "bg-blue-900/30 text-blue-400 border border-blue-800";
                let badgeText = "INFO";
                let icon = "info-circle";

                if (restante.startsWith("[WARN]")) {
                    badgeClass = "bg-amber-950/30 text-amber-400 border border-amber-800";
                    badgeText = "WARN";
                    icon = "exclamation-triangle";
                    restante = restante.substring(6).trim();
                } else if (restante.startsWith("[ERROR]")) {
                    badgeClass = "bg-red-950/30 text-red-400 border border-red-800";
                    badgeText = "ERROR";
                    icon = "times-circle";
                    restante = restante.substring(7).trim();
                } else if (restante.startsWith("[INFO]")) {
                    badgeText = "INFO";
                    restante = restante.substring(6).trim();
                }

                // 4. Renderizar el elemento visual
                div.innerHTML = `
                    <span class="text-gray-500 font-mono mr-3 shrink-0">${hora}</span>
                    <span class="text-teal-400 font-semibold font-mono mr-3 shrink-0"><i class="fas fa-user-shield mr-1"></i>${usuario}</span>
                    <span class="px-2 py-0.5 rounded text-xs font-semibold mr-3 ${badgeClass} shrink-0">
                        <i class="fas fa-${icon} mr-1"></i>${badgeText}
                    </span>
                    <span class="text-gray-200 font-medium">${restante}</span>
                `;

                fragment.appendChild(div);
            });

            // Limpiamos e inyectamos todo el bloque de golpe
            terminal.innerHTML = "";
            terminal.appendChild(fragment);
        })
        .catch(error => {
            console.error("Error al sincronizar los logs en vivo:", error);
            
            terminal.innerHTML = "";
            const offlineDiv = document.createElement("div");
            offlineDiv.className = "log-line text-red-500 flex items-center py-2 text-sm font-semibold";
            
            if (error.message === "403_FORBIDDEN") {
                offlineDiv.innerHTML = `<i class="fas fa-ban mr-2"></i> [ACCESO DENEGADO] No tienes roles suficientes (ADMIN/OWNER) para ver esta consola.`;
            } else {
                offlineDiv.innerHTML = `<i class="fas fa-exclamation-circle mr-2"></i> [CONEXIÓN PERDIDA] No se pudo conectar con el servidor de logs. Reintentando...`;
            }
            terminal.appendChild(offlineDiv);
        });
    }

    // Ejecuta la primera sincronización de inmediato al cargar la página
    actualizarLogs();

    // Consulta de forma automática cada 3 segundos
    const intervalId = setInterval(actualizarLogs, 3000);


    // =========================================================================
    // 🗄️ MANEJO DEL BOTÓN DE BACKUP (Dentro del cargador del DOM para evitar nulos)
    // =========================================================================
    if (btnBackup) {
        btnBackup.addEventListener('click', function() {
            const btn = this;
            btn.disabled = true;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> Generando...';

            // 🌟 CORREGIDO: Cambiado a método 'GET' para alinearse perfectamente con tu controlador de Spring Boot
            fetch('/monitoring/monitoring-api/backup/generate', {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(response => {
                if (response.status === 403) {
                    throw new Error("No tienes permisos de Administrador para realizar copias de seguridad.");
                }
                if (!response.ok) {
                    throw new Error("Error en el servidor al compilar el archivo SQL.");
                }
                return response.text();
            })
            .then(data => {
                alert("¡Excelente! " + data);
            })
            .catch(error => {
                console.error('Error:', error);
                alert("Ocurrió un error: " + error.message);
            })
            .finally(() => {
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-database mr-2"></i> Generar Backup Manual (SQL)';
            });
        });
    }
});