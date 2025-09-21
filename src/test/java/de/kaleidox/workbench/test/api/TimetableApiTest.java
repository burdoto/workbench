package de.kaleidox.workbench.test.api;

import de.kaleidox.workbench.repo.TimetableEntryRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TimetableApiTest {
    @Autowired private TimetableEntryRepository entries;
    @Autowired private TestRestTemplate         rest;

    @Test
    @Order(0)
    void contextLoads() {
        assertNotNull(entries);
    }

    @Test
    @Order(10)
    void createTimetableEntries() {
    }
}
