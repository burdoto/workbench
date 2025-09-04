package de.warmulla_elektro.workbench.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Embeddable
@EqualsAndHashCode(of = "time")
public class Interruption {
    LocalDateTime time;
    Duration      duration;
}
