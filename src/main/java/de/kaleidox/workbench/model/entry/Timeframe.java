package de.kaleidox.workbench.model.entry;

import de.kaleidox.workbench.util.Exceptions;

import java.time.LocalDateTime;

public interface Timeframe {
    static void validateTimeframe(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) throw Exceptions.invalidTimeframe();
    }

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();
}
