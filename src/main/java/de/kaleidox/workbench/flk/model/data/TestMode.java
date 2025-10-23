package de.kaleidox.workbench.flk.model.data;

import lombok.Value;

@Value
public class TestMode {
    int    id;
    String desc;

    @Override
    public String toString() {
        return "%s (%d)".formatted(desc, id);
    }
}
