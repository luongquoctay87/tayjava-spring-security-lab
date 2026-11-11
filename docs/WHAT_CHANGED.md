# lesson-32-otp-provider

- **Parent:** `lesson-29-resource-server`
- **What changed:** Custom `AuthenticationProvider` stack for two-step login — `MfaAwareAuthenticationProvider` wraps Dao for INSTRUCTOR/ADMIN, opaque MFA ticket + `OtpAuthenticationProvider` on `POST /api/auth/mfa/verify`; lab OTP via `tayjava.mfa.lab-code` (no external authenticator)

Run: `./mvnw -q test`
