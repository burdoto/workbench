package de.kaleidox.workbench.model.jpa.inventory;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ItemDelta {
    @ManyToOne Item item;
    Type   type;
    double amount;

    enum Type {
        Have, Into_Warehouse, From_Warehouse
    }
}
