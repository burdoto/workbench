package de.warmulla_elektro.workbench;

import org.comroid.api.data.seri.adp.Jackson;
import org.comroid.api.net.REST;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
public class TimetableApiTest {
    private static final Random RNG = new Random();

    @Test
    public void createRandomTimeTableEntry() {
        var id = UUID.randomUUID();

        var startTime            = LocalDateTime.now().minus(Duration.ofHours(RNG.nextInt(4)));
        var endTime              = LocalDateTime.now().plus(Duration.ofHours(RNG.nextInt(4)));
        var customerName         = UUID.randomUUID().toString();
        var notes                = UUID.randomUUID().toString();
        var interruptionTime     = LocalDateTime.now();
        var interruptionDuration = Duration.ofMinutes(RNG.nextInt(30));
        var assignmentUsername   = UUID.randomUUID().toString();
        var assignmentNotes      = UUID.randomUUID().toString();

        @Language("json") var entryJson = """
                {
                  "id": "%s",
                  "startTime" : "%s",
                  "endTime" : "%s",
                  "customerName": "%s",
                  "notes" : "%s",
                  "interruptions" : [
                    {
                      "time": "%s",
                      "duration": "%s"
                    }
                  ],
                  "assignments" : [
                    {
                      "user": {
                        "username": "%s"
                      },
                      "notes": "%s"
                    }
                  ]
                }
                """;

        var data = REST.put("http://localhost:8080/api/timetableEntries/" + id,
                        Jackson.JSON.parse(entryJson.formatted(id,
                                startTime,
                                endTime,
                                customerName,
                                notes,
                                interruptionTime,
                                interruptionDuration,
                                assignmentUsername,
                                assignmentNotes)))
                .thenApply(REST.Response::validate2xxOK)
                .thenApply(REST.Response::getBody)
                .join();

        System.out.println("data = " + data.toSerializedString());
    }
}
