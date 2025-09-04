package de.warmulla_elektro.workbench.model.config;

public record AppConfig(DatabaseInfo database, OAuth2Info[] oauth) {}
