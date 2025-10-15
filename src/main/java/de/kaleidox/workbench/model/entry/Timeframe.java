package de.kaleidox.workbench.model.entry;

import java.time.LocalDateTime;

public interface Timeframe {
    static void validateTimeframe(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) throw new IllegalArgumentException("Start Time cannot be after End Time");
    }

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();
}
