package com.eda.shippingService.model;

import com.eda.shippingService.domain.entity.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {
    @Test
    public void shouldBeEqual() {
        Address address1 = new Address("street", "city", "state", "postalCode", "country");
        Address address2 = new Address("street", "city", "state", "postalCode", "country");
        assertEquals(address1, address2);
    }
    @Test
    public void shouldBeDifferent(){
        Address address1 = new Address("street", "city", "state", "postalCode", "country");
        Address address2 = new Address("street", "city", "state", "postalCode", "country2");
        assertNotEquals(address1, address2);
    }

    @Test
    public void shouldValidate(){
        Address address = new Address("street", "city", "state", "postalCode", "DE");
        assertTrue(address.validate());
    }

    @Test    public void shouldNotValidate(){
        Address address = new Address("street", "city", "state", "postalCode", "US");
        assertFalse(address.validate());
    }
}
