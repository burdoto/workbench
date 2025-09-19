package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "name")
public class Customer {
    @Id String name;

    @Override
    public String toString() {
        return name;
    }
}
