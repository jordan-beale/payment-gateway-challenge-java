package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;

import static java.util.Objects.requireNonNull;

/**
 * Concrete {@link Payment} representation used for persistence and JSON serialization.
 *
 * <p>The JSON wire format uses snake_case keys (for example {@code last_four_digits},
 * {@code expiry_month}) as defined by the {@link JsonProperty} annotations on each component.
 *
 * @param id unique identifier assigned by the gateway when the payment is persisted
 * @param status outcome of the bank authorization step
 * @param lastFourDigits last four digits of the card number, stored for display and reconciliation
 * @param expiryMonth card expiry month in the range {@code 1}-{@code 12}
 * @param expiryYear four-digit card expiry year
 * @param currency ISO 4217 currency code of the payment
 * @param amount payment amount in the currency's minor units
 */
public record PaymentRecord(
    @JsonProperty("id") String id,
    @JsonProperty("status") Status status,
    @JsonProperty("last_four_digits") String lastFourDigits,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount
) implements Payment {

  /**
   * Creates a validated {@code PaymentRecord}.
   *
   * @param id unique identifier assigned by the gateway
   * @param status authorization outcome
   * @param lastFourDigits last four digits of the card number
   * @param expiryMonth card expiry month
   * @param expiryYear four-digit card expiry year
   * @param currency ISO 4217 currency code
   * @param amount payment amount in minor units
   * @return a new validated {@code PaymentRecord}
   * @throws NullPointerException if {@code id}, {@code status}, {@code lastFourDigits} or
   *     {@code currency} is {@code null}
   * @throws IllegalArgumentException if {@code lastFourDigits} is not exactly four numeric digits,
   *     if {@code expiryMonth} is outside {@code 1}-{@code 12}, if the {@code expiryYear}/
   *     {@code expiryMonth} combination is not in the future, if {@code currency} is not one of
   *     the values supported by {@link Currency}, or if {@code amount} is not positive
   */
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