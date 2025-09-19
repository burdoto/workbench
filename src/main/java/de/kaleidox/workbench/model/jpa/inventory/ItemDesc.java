package de.kaleidox.workbench.model.jpa.inventory;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Embeddable
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "detail" }) })
public class ItemDesc {
    String name;
    String detail;
    String unit;
}
