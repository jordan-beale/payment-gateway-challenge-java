package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;

import static java.util.Objects.requireNonNull;

public record PaymentRequestRecord(
    @JsonProperty("card_number") String cardNumber,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements PaymentRequest {

  public static PaymentRequestRecord of(final String cardNumber,
      final int expiryMonth,
      final int expiryYear,
      final String currency,
      final long amount,
      final String cvv) {
    requireNonNull(cardNumber, "Card number required");
    requireNonNull(currency, "Currency required");
    requireNonNull(cvv, "CVV required");

    if (!cardNumber.matches("\\d{14,19}")) {
      throw new IllegalArgumentException("Card number must be 14-19 numeric digits");
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
    if (!cvv.matches("\\d{3,4}")) {
      throw new IllegalArgumentException("CVV must be 3-4 numeric digits");
    }

    return new PaymentRequestRecord(cardNumber, expiryMonth, expiryYear, currency, amount, cvv);
  }
}