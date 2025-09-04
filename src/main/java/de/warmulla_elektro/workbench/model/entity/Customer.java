package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "name")
@Table(name = "workbench_customers")
public class Customer {
    @Id String name;
}
