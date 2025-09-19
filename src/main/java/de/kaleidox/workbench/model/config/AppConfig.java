package de.kaleidox.workbench.model.config;

public record AppConfig(DatabaseInfo database, OAuth2Info[] oauth) {}
