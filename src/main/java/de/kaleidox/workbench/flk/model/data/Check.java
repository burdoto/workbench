package de.kaleidox.workbench.flk.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.comroid.units.UValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class Check {
    String  name;
    boolean passed;
    @Singular List<UValue> values;
    @Nullable UValue       limit;

    @JsonIgnore
    public String getValuesString() {
        return values.stream().map(UValue::toString).collect(Collectors.joining(", "));
    }

    @JsonIgnore
    public String getPassedString() {
        return passed ? "passed" : "failed";
    }
}
