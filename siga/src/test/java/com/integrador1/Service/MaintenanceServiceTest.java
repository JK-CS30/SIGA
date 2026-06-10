package com.integrador1.Service;

import com.integrador1.model.Maintenance;
import com.integrador1.repository.MaintenanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaintenanceServiceTest {

    @Test
    void deberiaContarMantenimientosAbiertos() {

        MaintenanceRepository repository =
                Mockito.mock(MaintenanceRepository.class);

        Maintenance m1 = new Maintenance();
        m1.setStatus("ABIERTO");

        Maintenance m2 = new Maintenance();
        m2.setStatus("CERRADO");

        Mockito.when(repository.findAll())
                .thenReturn(List.of(m1, m2));

        long abiertos =
                repository.findAll()
                        .stream()
                        .filter(m ->
                                m.getStatus()
                                        .equals("ABIERTO"))
                        .count();

        assertEquals(1, abiertos);
    }
}
