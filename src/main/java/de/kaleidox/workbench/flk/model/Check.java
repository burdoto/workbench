package de.kaleidox.workbench.flk.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.comroid.units.UValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
@Builder
public class Check {
    String  name;
    boolean passed;
    @Singular List<UValue> values;
    @Nullable UValue       limit;
}
