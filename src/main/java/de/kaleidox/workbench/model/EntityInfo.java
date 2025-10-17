package de.kaleidox.workbench.model;

import org.comroid.api.data.seri.StringSerializable;
import org.comroid.api.data.seri.adp.StringSerializationProvider;

import java.util.Optional;

public interface EntityInfo extends StringSerializable {
    Object toCompositeKey();

    interface Provider<T extends EntityInfo> extends StringSerializationProvider<T> {
        Optional<?> findById(T info);
    }
}
