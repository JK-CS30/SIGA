package com.integrador1.Service;

import com.integrador1.model.Equipment;
import com.integrador1.repository.EquipmentRepository;
import com.integrador1.service.EquipmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentServiceTest {

    @Test
    void deberiaListarEquipos() {

        EquipmentRepository repository =
                Mockito.mock(EquipmentRepository.class);

        EquipmentService service =
                new EquipmentService(repository);

        Equipment e = new Equipment();
        e.setCode("EQ-001");

        Mockito.when(repository.findAll())
                .thenReturn(List.of(e));

        List<Equipment> resultado =
                service.listarEquipos();

        assertEquals(1, resultado.size());
        assertEquals("EQ-001",
                resultado.get(0).getCode());
    }
}