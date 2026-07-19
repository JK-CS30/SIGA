package com.integrador1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackupService.class);

    // Inyectamos las credenciales desde tu application.yml
    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    public String generateBackup() {
        // 1. Obtener el nombre de la base de datos de la URL de conexión
        String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
        if (dbName.contains("?")) {
            dbName = dbName.substring(0, dbName.indexOf("?"));
        }

        // 2. Definir la ruta de destino y nombre de archivo con fecha/hora actual
        String backupDir = "backups";
        File directorio = new File(backupDir);
        if (!directorio.exists()) {
            boolean creado = directorio.mkdirs(); // Crea la carpeta 'backups' en la raíz si no existe
            if (creado) {
                log.info("[BACKUP] Directorio 'backups' creado por primera vez.");
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = backupDir + "/backup_" + dbName + "_" + timestamp + ".sql";

        log.info("[BACKUP] Iniciando copia de seguridad para la base de datos: [{}]", dbName);

        // 3. Ruta absoluta al ejecutable pg_dump de PostgreSQL en Windows
        String rutaPgDump = "C:\\Program Files\\PostgreSQL\\18\\bin\\pg_dump.exe"; 
        
        File archivoPgDump = new File(rutaPgDump);
        String comandoDump = archivoPgDump.exists() ? rutaPgDump : "pg_dump";

        ProcessBuilder pb = new ProcessBuilder(
                comandoDump,
                "-U", dbUser,
                "-h", "localhost",
                "-p", "5432",
                "-F", "p", // Formato plano (.sql)
                "-b",      // Incluye blobs grandes
                "-v",      // Verbose (detallado)
                "-f", nombreArchivo,
                dbName
        );

        pb.environment().put("PGPASSWORD", "Kevin_324"); 

        try {
            Process proceso = pb.start();
            int codigoSalida = proceso.waitFor();

            if (codigoSalida == 0) {
                log.info("Copia de seguridad del sistema generada exitosamente en el archivo: {}", nombreArchivo);
                return "Backup exitoso guardado en: " + nombreArchivo;
            } else {
                log.error("[BACKUP] Error al generar backup. Código de salida del sistema: {}", codigoSalida);
                return "Error en el comando pg_dump. Código de salida: " + codigoSalida;
            }
        } catch (IOException | InterruptedException e) {
            log.error("[BACKUP] Excepción crítica al ejecutar backup", e);
            Thread.currentThread().interrupt();
            return "Excepción durante el proceso: " + e.getMessage();
        }
    }
}