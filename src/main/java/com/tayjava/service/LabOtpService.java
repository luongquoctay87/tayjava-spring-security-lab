package com.tayjava.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Lesson 32: lab-only OTP verifier. Production would use TOTP against a per-user secret; tests and
 * demos use a fixed code from config so no external authenticator is required.
 */
@Service
public class LabOtpService {

  private final byte[] expectedDigest;

  public LabOtpService(@Value("${tayjava.mfa.lab-code:123456}") String labCode) {
    this.expectedDigest = sha256(labCode);
  }

  public boolean matches(String code) {
    if (code == null) {
      return false;
    }
    return MessageDigest.isEqual(expectedDigest, sha256(code.trim()));
  }

  private static byte[] sha256(String value) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 not available", ex);
    }
  }
}
