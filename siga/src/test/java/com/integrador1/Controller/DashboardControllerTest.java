package com.integrador1.Controller;

import com.integrador1.controller.DashboardController;
import com.integrador1.service.EquipmentService;
import com.integrador1.service.MaintenanceService;
import com.integrador1.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private MaintenanceService maintenanceService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void shouldReturnDashboardView() throws Exception {

        when(equipmentService.listarEquipos()).thenReturn(java.util.List.of());
        when(rentalService.listRentals()).thenReturn(java.util.List.of());
        when(maintenanceService.listMaintenance()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }
}