package de.kaleidox.workbench.model.jpa.representant.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Customer {
    @Id         String                 name;
    @ManyToMany Collection<Department> departments;

    public Optional<Department> findDepartment(String name) {
        return departments.stream().filter(department -> department.getName().equals(name)).findAny();
    }
}
