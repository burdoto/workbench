package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@IdClass(Customer.CompositeKey.class)
public class Customer {
    @Id String name;
    @Id String department;

    @Override
    public String toString() {
        return name;
    }

    public CompositeKey key() {
        return new CompositeKey(name, department);
    }

    @Embeddable
    public record CompositeKey(String name, String department) {}
}
