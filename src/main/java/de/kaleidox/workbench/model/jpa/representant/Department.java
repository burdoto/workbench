package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "customer_name" }) })
public class Department {
    @Id String name;
}
