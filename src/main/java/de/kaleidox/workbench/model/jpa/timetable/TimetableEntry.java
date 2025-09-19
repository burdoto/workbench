package de.kaleidox.workbench.model.jpa.timetable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.model.jpa.representant.User;
import jakarta.persistence.Basic;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.comroid.annotations.Readonly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(of = "id")
public class TimetableEntry {
    public static final DateTimeFormatter        DATE_FORMATTER = DateTimeFormatter.ofPattern("EE dd.MM.yy");
    public static final DateTimeFormatter        HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    @Readonly @Id       UUID                     id             = UUID.randomUUID();
    @ManyToOne          Customer                 customer;
    @Basic              LocalDateTime            startTime;
    @Basic              LocalDateTime            endTime;
    @Nullable           String                   notes;
    @ManyToOne          User                     createdBy;
    @ElementCollection  Collection<Interruption> interruptions  = new ArrayList<>();
    @ElementCollection  Collection<Assignment>   assignments    = new ArrayList<>();

    @JsonIgnore
    public String getDayText() {
        return startTime.format(DATE_FORMATTER);
    }

    @JsonIgnore
    public String getStartTimeText() {
        return startTime.format(HOUR_FORMATTER);
    }

    @JsonIgnore
    public String getEndTimeText() {
        return endTime.format(HOUR_FORMATTER);
    }

    @JsonIgnore
    public String getBreaksSummaryText() {
        return switch (interruptions.size()) {
            case 0 -> "Keine";
            case 1 -> interruptions.stream().findAny().orElseThrow().toString();
            default -> "%d...".formatted(interruptions.size());
        };
    }

    @JsonIgnore
    public String getAssignmentsSummaryText() {
        return switch (assignments.size()) {
            case 0 -> "Keine";
            case 1 -> assignments.stream().findAny().orElseThrow().toString();
            default -> "%d...".formatted(assignments.size());
        };
    }

    public record CreateData(
            @NotNull String customerName,
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            @Nullable String notes
    ) {}
}
