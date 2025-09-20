package de.kaleidox.workbench.test.api;

import de.kaleidox.workbench.model.jpa.representant.User;
import de.kaleidox.workbench.repo.UserRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserApiTest {
    public static final User USER_1 = new User("user1", "User One");
    public static final User USER_2 = new User("user2", "User Two");

    @Autowired private UserRepository   users;
    @Autowired private TestRestTemplate rest;

    @Test
    @Order(Integer.MIN_VALUE)
    void contextLoads() {
        assertNotNull(users);
    }

    @Test
    @Order(1)
    void fetchSelf() {
        var self = rest.getForEntity("http://localhost:8080/api/me", User.class).getBody();

        assertNotNull(self, "self not found");
        assertEquals(User.DEV.getUsername(), self.getUsername(), "username mismatch");
        assertEquals(User.DEV.getDisplayName(), self.getDisplayName(), "displayname mismatch");
    }

    @Test
    @Order(10)
    void createUsers() {
        for (var user : List.of(USER_1, USER_2)) {
            var response = rest.postForEntity("http://localhost:8080/api/users", user, Void.class);
            assertEquals(201, response.getStatusCode().value(), "status code mismatch");

            var result = users.findById(user.getUsername());
            assertTrue(result.isPresent(), "user not found");
            assertEquals(user, result.get());
        }
    }
}
