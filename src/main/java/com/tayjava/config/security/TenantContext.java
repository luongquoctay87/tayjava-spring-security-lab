package com.tayjava.config.security;

/** Lesson 34: request-scoped tenant (ThreadLocal — always clear in finally). */
public final class TenantContext {

  public static final String NONE = "__none__";

  private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

  private TenantContext() {}

  public static void set(String tenantId) {
    CURRENT.set(tenantId);
  }

  public static String get() {
    String value = CURRENT.get();
    return value == null ? NONE : value;
  }

  public static void clear() {
    CURRENT.remove();
  }
}
