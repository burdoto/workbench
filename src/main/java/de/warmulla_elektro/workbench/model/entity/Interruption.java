package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.comroid.api.Polyfill;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Embeddable
@EqualsAndHashCode(of = "time")
public class Interruption {
    LocalDateTime time;
    Duration      duration;

    @Override
    public String toString() {
        return "%s, %s".formatted(time.format(TimetableEntry.HOUR_FORMATTER), Polyfill.durationString(duration, 1));
    }
}
