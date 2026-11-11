package com.tayjava.demo;

import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Configuration
public class RegisteredClientConfig {

  @Bean
  RegisteredClientRepository registeredClientRepository(
      PasswordEncoder encoder,
      @Value("${tayjava.oauth.web-client-secret}") String webSecret,
      @Value("${tayjava.oauth.partner-client-secret}") String partnerSecret) {

    RegisteredClient web =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("tayjava-web")
            .clientSecret(encoder.encode(webSecret))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8081/login/oauth2/code/tayjava")
            .postLogoutRedirectUri("http://127.0.0.1:8081/")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("courses.read")
            .scope("enrollments.read")
            .clientSettings(
                ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .requireProofKey(true)
                    .build())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(15))
                    .refreshTokenTimeToLive(Duration.ofDays(7))
                    .reuseRefreshTokens(false)
                    .build())
            .build();

    RegisteredClient mobile =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("tayjava-mobile")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("com.tayjava.app://oauth2/callback")
            .scope(OidcScopes.OPENID)
            .scope("courses.read")
            .clientSettings(ClientSettings.builder().requireProofKey(true).build())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(10))
                    .refreshTokenTimeToLive(Duration.ofDays(30))
                    .reuseRefreshTokens(false)
                    .build())
            .build();

    RegisteredClient partner =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("tayjava-partner-sync")
            .clientSecret(encoder.encode(partnerSecret))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("enrollments.read")
            .tokenSettings(
                TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(5)).build())
            .build();

    return new InMemoryRegisteredClientRepository(web, mobile, partner);
  }
}
