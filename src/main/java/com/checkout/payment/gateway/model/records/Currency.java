package com.checkout.payment.gateway.model.records;

import java.util.Arrays;

/**
 * ISO 4217 currency codes supported by the payment gateway.
 */
public enum Currency {
  /** United States dollar. */
  USD,
  /** Pound sterling. */
  GBP,
  /** Euro. */
  EUR;

  /**
   * Indicates whether the given code matches one of the supported currencies.
   *
   * @param value currency code to test, compared against the enum constant names exactly
   * @return {@code true} if {@code value} matches a supported {@link Currency}, {@code false}
   *     otherwise
   */
  public static boolean isSupported(final String value) {
    return Arrays.stream(values())
        .anyMatch(c -> c.name().equals(value));
  }
}