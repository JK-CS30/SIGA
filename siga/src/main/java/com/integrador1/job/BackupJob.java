package com.integrador1.job;

import com.integrador1.service.DatabaseBackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupJob {

    private static final Logger log = LoggerFactory.getLogger(BackupJob.class);
    private final DatabaseBackupService backupService;

    public BackupJob(DatabaseBackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "0 59 23 * * ?")
    public void ejecutarBackupProgramado() {
        log.info("[JOB] Iniciando tarea automática programada de Backup...");
        backupService.generateBackup();
    }

}