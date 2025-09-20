package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@IdClass(Customer.CompositeKey.class)
@EqualsAndHashCode(of = "name")
public class Customer {
    @Id String name;
    @Id String department;

    @Override
    public String toString() {
        return name;
    }

    @Embeddable
    public record CompositeKey(String name, String department) {}
}
