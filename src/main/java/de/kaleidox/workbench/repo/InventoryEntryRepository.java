package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.inventory.InventoryEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.UUID;

public interface InventoryEntryRepository extends CrudRepository<InventoryEntry, UUID> {
    Collection<InventoryEntry> findByCustomerName(String name);
}
