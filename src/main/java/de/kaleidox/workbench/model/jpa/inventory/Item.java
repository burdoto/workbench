package de.kaleidox.workbench.model.jpa.inventory;

import de.kaleidox.workbench.model.jpa.representant.market.ItemManufacturer;
import de.kaleidox.workbench.model.jpa.representant.market.ItemSupplier;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
public class Item {
    @Id         ItemDesc                     key;
    @ManyToMany Collection<ItemSupplier>     suppliers     = new ArrayList<>();
    @ManyToMany Collection<ItemManufacturer> manufacturers = new ArrayList<>();
}
