package com.checkout.payment.gateway.model.records;

import java.util.Arrays;

public enum Currency {
  USD,
  GBP,
  EUR;

  public static boolean isSupported(final String value) {
    return Arrays.stream(values())
        .anyMatch(c -> c.name().equals(value));
  }
}