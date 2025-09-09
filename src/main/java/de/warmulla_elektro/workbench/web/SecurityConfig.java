package de.warmulla_elektro.workbench.web;

import de.warmulla_elektro.workbench.model.config.AppConfig;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Log
@Configuration
public class SecurityConfig {
    @Bean
    @ConditionalOnExpression("#{!(systemEnvironment['DEBUG']?:'false').equals('true')}")
    public @Nullable ClientRegistrationRepository clientRegistrationRepository(@Autowired AppConfig config) {
        var registrations = Arrays.stream(config.oauth())
                .filter(oAuth -> !oAuth.name().isBlank())
                .map(info -> ClientRegistration.withRegistrationId(info.name())
                        .clientId(info.clientId())
                        .clientSecret(info.secret())
                        .scope(info.scope())
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri(info.redirectUrl())
                        .authorizationUri(info.authorizationUrl())
                        .tokenUri(info.tokenUrl())
                        .userInfoUri(info.userInfoUrl())
                        .userNameAttributeName(info.userNameAttributeName())
                        .build())
                .toArray(ClientRegistration[]::new);
        return registrations.length == 0 ? null : new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    @ConditionalOnBean(ClientRegistrationRepository.class)
    @ConditionalOnMissingBean(type = "org.springframework.boot.test.mock.mockito.MockitoPostProcessor")
    public SecurityFilterChain configureSecure(HttpSecurity http) throws Exception {
        log.info("Using OAuth2-based SecurityFilterChain");
        return http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/**")
                .fullyAuthenticated()
                .anyRequest()
                .authenticated()).oauth2Login(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable).build();
    }

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public SecurityFilterChain configureInsecure(HttpSecurity http) throws Exception {
        log.warning("Using insecure SecurityFilterChain; consider configuring OAuth2 providers!");
        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
