package de.kaleidox.workbench.test.api;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.repo.CustomerRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CustomerApiTest {
    public static final String   FAARQUARDT_NAME   = "Lord Faarquardt";
    public static final Customer FAARQUARDT_CASTLE = new Customer(FAARQUARDT_NAME, "Castle");
    public static final Customer FAARQUARDT_HOUSE  = new Customer(FAARQUARDT_NAME, "House");

    @Autowired private CustomerRepository customers;
    @Autowired private TestRestTemplate   rest;

    @Test
    @Order(0)
    void contextLoads() {
        assertNotNull(customers);
    }

    @Test
    @Order(10)
    void createCustomers() {
        for (var customer : List.of(FAARQUARDT_CASTLE, FAARQUARDT_HOUSE)) {
            var response = rest.postForEntity("http://localhost:8080/api/customers", customer, Void.class);
            assertEquals(201, response.getStatusCode().value(), "status code mismatch");

            var result = customers.findById(customer.key());
            assertTrue(result.isPresent(), "customer not found");
            assertEquals(customer, result.get());
        }
    }

    @Test
    @Order(11)
    void fetchNames() {
        var response = rest.getForEntity("http://localhost:8080/api/customers/names", String[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful(), "request unsuccessful");

        var data = response.getBody();
        assertNotNull(data);
        assertEquals(1, data.length, "wrong array length");
        assertEquals(FAARQUARDT_NAME, data[0], "customer name mismatch");
    }

    @Test
    @Order(12)
    void fetchDepartments() {
        var response = rest.getForEntity("http://localhost:8080/api/customers/%s/departments".formatted(FAARQUARDT_NAME),
                String[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful(), "request unsuccessful");

        var data = response.getBody();
        assertNotNull(data);
        assertEquals(2, data.length, "wrong array length");
        assertNotEquals(-1, Arrays.binarySearch(data, "Castle"), "department not found");
        assertNotEquals(-1, Arrays.binarySearch(data, "House"), "department not found");
    }
}
