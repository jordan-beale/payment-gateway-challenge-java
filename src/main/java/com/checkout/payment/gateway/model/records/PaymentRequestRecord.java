package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;

import static java.util.Objects.requireNonNull;

/**
 * Concrete {@link PaymentRequest} representation deserialize from the inbound API payload.
 *
 * <p>The JSON wire format uses snake_case keys (for example {@code card_number},
 * {@code expiry_month}) as defined by the {@link JsonProperty} annotations on each component.
 *
 * @param cardNumber raw card number, expected to be 14-19 numeric digits
 * @param expiryMonth card expiry month in the range {@code 1}-{@code 12}
 * @param expiryYear four-digit card expiry year
 * @param currency ISO 4217 currency code of the requested payment
 * @param amount requested amount in the currency's minor units
 * @param cvv card verification value, expected to be 3 or 4 numeric digits
 */
public record PaymentRequestRecord(
    @JsonProperty("card_number") String cardNumber,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements PaymentRequest {

  /**
   * Creates a validated {@code PaymentRequestRecord}.
   *
   * @param cardNumber raw card number
   * @param expiryMonth card expiry month
   * @param expiryYear four-digit card expiry year
   * @param currency ISO 4217 currency code
   * @param amount requested amount in minor units
   * @param cvv card verification value
   * @return a new validated {@code PaymentRequestRecord}
   * @throws NullPointerException if {@code cardNumber}, {@code currency} or {@code cvv} is
   *     {@code null}
   * @throws IllegalArgumentException if {@code cardNumber} is not 14-19 numeric digits, if
   *     {@code expiryMonth} is outside {@code 1}-{@code 12}, if the {@code expiryYear}/
   *     {@code expiryMonth} combination is not in the future, if {@code currency} is not one of
   *     the values supported by {@link Currency}, if {@code amount} is not positive, or if
   *     {@code cvv} is not 3-4 numeric digits
   */
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