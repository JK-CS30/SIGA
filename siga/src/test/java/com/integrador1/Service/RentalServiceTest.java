package com.integrador1.Service;

import com.integrador1.model.Rental;
import com.integrador1.repository.RentalRepository;
import com.integrador1.service.RentalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RentalServiceTest {

    @Test
    void deberiaGuardarAlquiler() {

        RentalRepository repository =
                Mockito.mock(RentalRepository.class);

        RentalService service =
                new RentalService(repository);

        Rental rental = new Rental();
        rental.setCustomerName("Juan");

        Mockito.when(repository.save(rental))
                .thenReturn(rental);

        Rental resultado =
                repository.save(rental);

        assertEquals("Juan",
                resultado.getCustomerName());
    }

}
