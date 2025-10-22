package de.kaleidox.workbench.model.abstr;

import java.io.InputStream;
import java.util.stream.Stream;

public interface StorageService {
    /**
     * @param data the data to store
     *
     * @return an identifier to retrieve data
     */
    String store(InputStream data, String nameSuffix);

    /**
     * @param id data identifier
     *
     * @return whether the identifier exists in storage
     */
    boolean exists(String id);

    /**
     * @param id data identifier
     *
     * @return the data from storage
     */
    InputStream load(String id);

    /**
     * @return all existing data identifiers
     */
    Stream<String> all();
}
