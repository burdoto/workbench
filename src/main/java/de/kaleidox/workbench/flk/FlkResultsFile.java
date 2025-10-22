package de.kaleidox.workbench.flk;

import de.kaleidox.workbench.flk.model.Test;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FlkResultsFile {
    String deviceModel;
    String deviceSn;
    @Singular List<Test> tests;
}
