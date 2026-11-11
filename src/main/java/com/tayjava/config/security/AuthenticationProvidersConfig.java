package com.tayjava.config.security;

import com.tayjava.service.LabOtpService;
import com.tayjava.service.MfaTicketStore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Lesson 32: API login uses a dedicated {@link ProviderManager} (MFA-aware + OTP). The web form
 * chain keeps Spring Boot's default AuthenticationManager so session login is unchanged.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationProvidersConfig {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final MfaPolicy mfaPolicy;
  private final MfaTicketStore mfaTicketStore;
  private final LabOtpService labOtpService;

  @Bean
  @Qualifier("apiAuthenticationManager")
  AuthenticationManager apiAuthenticationManager() {
    DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailsService);
    dao.setPasswordEncoder(passwordEncoder);
    OtpAuthenticationProvider otp =
        new OtpAuthenticationProvider(mfaTicketStore, labOtpService, userDetailsService);
    return new ProviderManager(
        List.of(new MfaAwareAuthenticationProvider(dao, mfaPolicy), otp));
  }
}
