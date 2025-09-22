package de.kaleidox.workbench.test.api;

import de.kaleidox.workbench.model.jpa.representant.Department;
import de.kaleidox.workbench.repo.DepartmentRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Order(9)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DepartmentApiTest {
    public static final Department CASTLE = new Department("Castle");
    public static final Department HOUSE  = new Department("House");

    @Autowired private DepartmentRepository departments;
    @Autowired private TestRestTemplate     rest;

    @Test
    @Order(0)
    void contextLoads() {
        assertNotNull(departments);
    }

    @Test
    @Order(10)
    void createDepartments() {
        for (var department : List.of(CASTLE, HOUSE)) {
            var response = rest.postForEntity("http://localhost:8080/api/departments", department, Void.class);
            assertEquals(201, response.getStatusCode().value(), "status code mismatch");

            var result = departments.findById(department.getName());
            assertTrue(result.isPresent(), "department not found");
            assertEquals(department, result.get());
        }
    }
}
