package de.kaleidox.workbench.web;

import de.kaleidox.workbench.model.config.AppConfig;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Log
@Configuration
public class SecurityConfig extends DefaultOAuth2UserService {
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
                        .userNameAttributeName("userid")
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
                        .authenticated())
                .oauth2Login(oauth -> oauth.userInfoEndpoint(info -> info.userService(this)))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public SecurityFilterChain configureInsecure(HttpSecurity http) throws Exception {
        log.warning("Using insecure SecurityFilterChain; consider configuring OAuth2 providers!");
        return http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/**")
                .authenticated()
                .anyRequest()
                .permitAll()).httpBasic(Customizer.withDefaults()).userDetailsService(username -> new UserDetails() {
            // token for dev: ZGV2Og==

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return "{noop}";
            }

            @Override
            public String getUsername() {
                return username;
            }
        }).csrf(AbstractHttpConfigurer::disable).build();
    }

    @EventListener
    public void on(ApplicationStartedEvent ignored) {
        setAttributesConverter(input -> source -> {
            var userId = new SpelExpressionParser().parseRaw("ocs.data.id")
                    .getValue(SimpleEvaluationContext.forPropertyAccessors(new MapAccessor())
                            .withRootObject(source)
                            .build());

            source.put(input.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName(), userId);
            return source;
        });
    }
}
