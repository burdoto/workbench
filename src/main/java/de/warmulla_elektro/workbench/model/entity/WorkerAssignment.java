package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode(of = "user")
public class WorkerAssignment {
    @ManyToOne User user;
    String notes;

    @Override
    public String toString() {
        return user.getDisplayName();
    }
}
