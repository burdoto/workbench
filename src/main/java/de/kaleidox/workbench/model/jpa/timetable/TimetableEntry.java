package de.kaleidox.workbench.model.jpa.timetable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.kaleidox.workbench.controller.PopupController;
import de.kaleidox.workbench.model.EntityInfo;
import de.kaleidox.workbench.model.entry.Timeframe;
import de.kaleidox.workbench.model.jpa.representant.User;
import de.kaleidox.workbench.model.jpa.representant.customer.Customer;
import de.kaleidox.workbench.model.jpa.representant.customer.Department;
import de.kaleidox.workbench.repo.CustomerRepository;
import de.kaleidox.workbench.repo.TimetableEntryRepository;
import de.kaleidox.workbench.util.Exceptions;
import jakarta.persistence.Basic;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.comroid.annotations.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static de.kaleidox.workbench.util.ApplicationContextProvider.*;
import static java.time.format.DateTimeFormatter.*;
import static org.comroid.api.Polyfill.*;

@Data
@Entity
@IdClass(TimetableEntry.CompositeKey.class)
@EqualsAndHashCode(of = { "customer", "department", "startTime" })
public class TimetableEntry implements Timeframe, TimetableEntryReferent {
    public static final DateTimeFormatter DATE_FORMATTER = ofPattern("EE dd.MM.yy");
    public static final DateTimeFormatter HOUR_FORMATTER = ofPattern("HH:mm");

    @SuppressWarnings("unused")
    public static LocalDate parseDate(String str) {
        return LocalDate.from(DATE_FORMATTER.parse(str));
    }

    @SuppressWarnings("unused")
    public static LocalTime parseTime(String str) {
        return LocalTime.from(HOUR_FORMATTER.parse(str));
    }

    public static LocalDateTime parseDateTime(String str) {
        return LocalDateTime.parse(str);
    }

    @Id @ManyToOne     Customer                 customer;
    @Id @ManyToOne     Department               department;
    @Id @Basic         LocalDateTime            startTime;
    @Basic             LocalDateTime            endTime;
    @Nullable          String                   notes;
    @ManyToOne         User                     createdBy;
    @ElementCollection Collection<Interruption> interruptions = new ArrayList<>();
    @ElementCollection Collection<Assignment>   assignments   = new ArrayList<>();

    @JsonIgnore
    public String getViewUrlPath() {
        return "";
    }

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

    @JsonIgnore
    public CompositeKey key() {
        return new CompositeKey(this);
    }

    @JsonIgnore
    public Info info() {
        return new Info(this);
    }

    @Embeddable
    public record CompositeKey(
            @ManyToOne Customer customer, @ManyToOne Department department, LocalDateTime startTime
    ) {
        public CompositeKey(TimetableEntry entry) {
            this(entry.customer, entry.department, entry.startTime);
        }
    }

    public record Info(String customerName, String departmentName, LocalDateTime startTime) implements EntityInfo {
        public static Info parse(String str) {
            var split = str.split(",");
            return new Info(split[0], split[1], TimetableEntry.parseDateTime(split[2]));
        }

        public Info(TimetableEntry entry) {
            this(entry.customer.getName(), entry.department.getName(), entry.startTime);
        }

        @Override
        public String toSerializedString() {
            return "%s,%s,%s";
        }

        @Override
        public Object toCompositeKey() {
            var customer = bean(CustomerRepository.class).findById(customerName)
                    .orElseThrow(Exceptions::noSuchCustomer);
            return new CompositeKey(customer,
                    customer.findDepartment(departmentName).orElseThrow(Exceptions::noSuchDepartment),
                    startTime);
        }

        public enum KeyProvider implements EntityInfo.Provider<Info> {
            @Instance INSTANCE;

            KeyProvider() {
                PopupController.INFO_SERIALIZATION_PROVIDERS.put(TimetableEntryReferent.class, this);
            }

            @Override
            public Optional<?> findById(Info info) {
                return bean(TimetableEntryRepository.class).findById(uncheckedCast(info.toCompositeKey()));
            }

            @Override
            public String forward(Info info) {
                return info.toSerializedString();
            }

            @Override
            public Info backward(String str) {
                return parse(str);
            }
        }
    }

    public record CreateData(
            @NotNull String customerName,
            @NotNull String departmentName,
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            @Nullable String notes
    ) {
        public CreateData {
            Timeframe.validateTimeframe(startTime, endTime);
        }
    }
}
