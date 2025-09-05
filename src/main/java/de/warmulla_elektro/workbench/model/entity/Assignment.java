package de.warmulla_elektro.workbench.model.entity;

import de.warmulla_elektro.workbench.repo.UserRepository;
import de.warmulla_elektro.workbench.util.ApplicationContextProvider;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@Embeddable
@EqualsAndHashCode(of = "user")
public class Assignment {
    public static Assignment parse(String parse) {
        var split    = parse.split(": *");
        var username = split[0];
        var user = ApplicationContextProvider.bean(UserRepository.class).findById(username).orElseThrow();
        var notes    = split.length > 1 ? split[1] : null;
        return new Assignment().setUser(user).setNotes(notes);
    }

    @ManyToOne User   user;
    @Nullable  String notes;

    @Override
    public String toString() {
        return user.getDisplayName();
    }
}
