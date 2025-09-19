package de.kaleidox.workbench.model.jpa.representant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@EqualsAndHashCode(of = "name")
public class ItemSupplier {
    @Id        String                       name;
    @OneToMany Collection<ItemManufacturer> availableManufacturers = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
