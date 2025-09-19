package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
public class User {
    public static final User DEV = new User().setUsername("dev").setDisplayName("Developer");

    public static User create(String username) {
        return new User(username, username);
    }

    @Id String username;
    String displayName;
}
