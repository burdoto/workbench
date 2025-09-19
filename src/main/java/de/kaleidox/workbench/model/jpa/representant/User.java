package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "username")
public class User {
    public static final User   DEV = new User().setUsername("dev").setDisplayName("Developer");
    @Id                 String username;
    String displayName;
}
