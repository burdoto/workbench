package de.kaleidox.workbench.model.jpa.timetable;

import de.kaleidox.workbench.model.jpa.representant.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.comroid.api.Polyfill;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "time")
public class Interruption {
    public static Interruption parse(String parse) {
        var split    = parse.split(", ");
        var time     = TimetableEntry.HOUR_FORMATTER.parse(split[0]);
        var duration = Polyfill.parseDuration(split[1]);
        return new Interruption().setTime(LocalDateTime.from(time)).setDuration(duration);
    }

    LocalDateTime time;
    Duration      duration;
    @ManyToOne User createdBy;

    @Override
    public String toString() {
        return "%s, %s".formatted(time.format(TimetableEntry.HOUR_FORMATTER), Polyfill.durationString(duration, 1));
    }
}
