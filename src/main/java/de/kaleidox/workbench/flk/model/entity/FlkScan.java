package de.kaleidox.workbench.flk.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class FlkScan {
    @Id String sourceFile;
    String        resultFile;
    LocalDateTime timestamp = LocalDateTime.now();

    public FlkScan(String sourceFile, String resultFile) {
        this.sourceFile = sourceFile;
        this.resultFile = resultFile;
    }
}
