package de.warmulla_elektro.workbench.model.entity;

import de.warmulla_elektro.workbench.repo.UserRepo;
import de.warmulla_elektro.workbench.util.ApplicationContextProvider;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@Embeddable
@EqualsAndHashCode(of = "user")
public class WorkerAssignment {
    public static WorkerAssignment parse(String parse) {
        var split    = parse.split(": *");
        var username = split[0];
        var user     = ApplicationContextProvider.bean(UserRepo.class).findById(username).orElseThrow();
        var notes    = split.length > 1 ? split[1] : null;
        return new WorkerAssignment().setUser(user).setNotes(notes);
    }

    @ManyToOne User   user;
    @Nullable  String notes;

    @Override
    public String toString() {
        return user.getDisplayName();
    }
}
