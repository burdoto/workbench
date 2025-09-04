package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "workbench_users")
@EqualsAndHashCode(of = "username")
public class User {
    public static final User   DEV = new User().setUsername("dev").setDisplayName("Developer");
    @Id                 String username;
    String displayName;
}
