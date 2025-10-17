package de.kaleidox.workbench.util;

public class Exceptions {
    public static IllegalArgumentException invalidTimeframe() {
        return new IllegalArgumentException("Start Time cannot be after End Time");
    }

    public static IllegalArgumentException noSuchDtype() {
        return new IllegalArgumentException("Unrecognized entity type");
    }

    public static IllegalArgumentException noSuchUser() {
        return new IllegalArgumentException("User must be set");
    }

    public static IllegalArgumentException noSuchCustomer() {
        return new IllegalArgumentException("Customer was not found");
    }

    public static IllegalArgumentException noSuchDepartment() {
        return new IllegalArgumentException("Customer department was not found");
    }
}
