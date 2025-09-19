package de.kaleidox.workbench.model.jpa.inventory;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@Entity
public class InventoryEntry {
    @Id        UUID     id = UUID.randomUUID();
    @ManyToOne Customer customer;
    ItemDelta item;
    @Nullable String notes;
}
