package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.inventory.Item;
import de.kaleidox.workbench.model.jpa.inventory.ItemDesc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, ItemDesc> {
}
