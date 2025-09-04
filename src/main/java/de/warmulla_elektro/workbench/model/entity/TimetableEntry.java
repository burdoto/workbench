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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "workbench_timetable_entries")
public class TimetableEntry {
    @Readonly @Id UUID     id = UUID.randomUUID();
    @ManyToOne    Customer customer;
    LocalDateTime startTime;
    LocalDateTime endTime;
    @ElementCollection @CollectionTable(name = "workbench_timetable_interruptions")
    Collection<Interruption> interruptions;
    String notes;
    @ElementCollection @CollectionTable(name = "workbench_timetable_assignments")
    Collection<WorkerAssignment> assignments;
}
