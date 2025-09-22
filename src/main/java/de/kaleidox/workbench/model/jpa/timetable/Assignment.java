package de.kaleidox.workbench.model.jpa.timetable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.kaleidox.workbench.model.jpa.representant.User;
import de.kaleidox.workbench.repo.UserRepository;
import de.kaleidox.workbench.util.ApplicationContextProvider;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

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

    @ManyToOne User          user;
    @Nullable  LocalDateTime startTime;
    @Nullable  LocalDateTime endTime;
    @Nullable  String        notes;
    @ManyToOne User          createdBy;

    @JsonIgnore
    public String getStartTimeText() {
        return startTime == null ? "(selbe wie oben)" : startTime.format(TimetableEntry.HOUR_FORMATTER);
    }

    @JsonIgnore
    public String getEndTimeText() {
        return endTime == null ? "(selbe wie oben)" : endTime.format(TimetableEntry.HOUR_FORMATTER);
    }

    @Override
    public String toString() {
        return user.getDisplayName();
    }

    public record CreateData(
            @NotNull TimetableEntry.Info entryInfo,
            @NotNull String username,
            @Nullable LocalDateTime startTime,
            @Nullable LocalDateTime endTime,
            @Nullable String notes
    ) {}
}
