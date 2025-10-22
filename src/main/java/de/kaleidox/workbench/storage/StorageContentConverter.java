package de.kaleidox.workbench.storage;

public interface StorageContentConverter {
    /**
     * @param id input data identifier. must use extension {@code flk}
     *
     * @return result data identifier. uses extension {@code json}
     */
    String convert(String id);
}
