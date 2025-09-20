package de.kaleidox.workbench.test.api;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.repo.CustomerRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CustomerApiTest {
    public static final String   FAARQUARDT_NAME   = "Lord Faarquardt";
    public static final Customer FAARQUARDT_CASTLE = new Customer(FAARQUARDT_NAME, "Castle");
    public static final Customer FAARQUARDT_HOUSE  = new Customer(FAARQUARDT_NAME, "House");

    @Autowired
    private CustomerRepository customers;
    @Autowired
    private TestRestTemplate   rest;

    @Test
    @Order(0)
    void contextLoads() {
        assertNotNull(customers);
    }

    @Test
    @Order(10)
    public void createCustomers() {
        for (var customer : List.of(FAARQUARDT_CASTLE, FAARQUARDT_HOUSE)) {
            var response = rest.postForEntity("http://localhost:8080/api/customers", customer, Void.class);
            assertEquals(201, response.getStatusCode().value(), "status code mismatch");

            var result = customers.findById(customer.key());
            assertTrue(result.isPresent(), "customer not found");
            assertEquals(customer, result.get());
        }
    }
}
