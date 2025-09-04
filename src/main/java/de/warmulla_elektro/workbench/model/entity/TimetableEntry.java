package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.comroid.annotations.Readonly;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "workbench_timetable_entries")
public class TimetableEntry {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EE dd.MM.yy");
    public static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    @Readonly @Id       UUID              id             = UUID.randomUUID();
    @ManyToOne          Customer          customer;
    LocalDateTime startTime;
    LocalDateTime endTime;
    @ElementCollection @CollectionTable(name = "workbench_timetable_interruptions")
    Collection<Interruption> interruptions;
    @Nullable String notes;
    @ElementCollection @CollectionTable(name = "workbench_timetable_assignments")
    Collection<WorkerAssignment> assignments;
    @ManyToOne User createdBy;

    public String getDayText() {
        return startTime.format(DATE_FORMATTER);
    }

    public String getStartTimeText() {
        return startTime.format(HOUR_FORMATTER);
    }

    public String getEndTimeText() {
        return endTime.format(HOUR_FORMATTER);
    }

    public String getBreaksSummaryText() {
        return switch (interruptions.size()) {
            case 0 -> "Keine";
            case 1 -> interruptions.stream().findAny().orElseThrow().toString();
            default -> "%d...".formatted(interruptions.size());
        };
    }

    public String getAssignmentsSummaryText() {
        return switch (assignments.size()) {
            case 0 -> "Keine";
            case 1 -> assignments.stream().findAny().orElseThrow().toString();
            default -> "%d...".formatted(assignments.size());
        };
    }
}
