package de.kaleidox.workbench.model.config;

public record OAuth2Info(
        String name,
        String clientId,
        String secret,
        String[] scope,
        String redirectUrl,
        String authorizationUrl,
        String tokenUrl,
        String userInfoUrl
) {}
