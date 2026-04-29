package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;

import static java.util.Objects.requireNonNull;

public record PaymentRecord(
    @JsonProperty("id") String id,
    @JsonProperty("status") Status status,
    @JsonProperty("last_four_digits") String lastFourDigits,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount
) implements Payment {

  public static PaymentRecord of(final String id,
      final Status status,
      final String lastFourDigits,
      final int expiryMonth,
      final int expiryYear,
      final String currency,
      final long amount) {
    requireNonNull(id, "ID required");
    requireNonNull(status, "Status required");
    requireNonNull(lastFourDigits, "Last four digits required");

    if (!lastFourDigits.matches("\\d{4}")) {
      throw new IllegalArgumentException("Last four digits must be numeric");
    }
    if (expiryMonth < 1 || expiryMonth > 12) {
      throw new IllegalArgumentException("Expiry month must be between 1 and 12");
    }
    if (YearMonth.of(expiryYear, expiryMonth).isBefore(YearMonth.now())) {
      throw new IllegalArgumentException("Card expiry must be in the future");
    }
    if (!Currency.isSupported(currency.toUpperCase())) {
      throw new IllegalArgumentException(
          "Currency must be one of: " + java.util.Arrays.toString(Currency.values()));
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be a positive integer");
    }

    return new PaymentRecord(id, status, lastFourDigits, expiryMonth, expiryYear, currency, amount);
  }
}