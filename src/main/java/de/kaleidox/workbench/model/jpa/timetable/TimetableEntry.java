package de.kaleidox.workbench.model.jpa.timetable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.model.jpa.representant.User;
import jakarta.persistence.Basic;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static java.time.format.DateTimeFormatter.*;

@Data
@Entity
@IdClass(TimetableEntry.CompositeKey.class)
@EqualsAndHashCode(of = { "customer", "startTime" })
public class TimetableEntry {
    public static final DateTimeFormatter DATE_FORMATTER = ofPattern("EE dd.MM.yy");
    public static final DateTimeFormatter HOUR_FORMATTER = ofPattern("HH:mm");
    @Id @ManyToOne      Customer          customer;
    @Id @Basic          LocalDateTime     startTime;
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

    @Embeddable
    public record CompositeKey(
            @ManyToOne Customer customer, LocalDateTime startTime
    ) {}

    public record Info(Customer.CompositeKey customerInfo, LocalDateTime startTime) {}

    public record CreateData(
            @NotNull Customer.CompositeKey customerInfo,
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            @Nullable String notes
    ) {}
}
