package de.kaleidox.workbench.flk.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Value
@Builder
public class Test {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yy");
    int       number;
    LocalDate date;
    int       applianceNumber;
    TestMode  testMode;
    String    site;
    String    user;
    @Singular List<Check> checks;
    String description;
    String location;
    String text;
}
