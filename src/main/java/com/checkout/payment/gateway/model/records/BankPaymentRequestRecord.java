package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * Concrete {@link BankPaymentRequest} representation sent to the downstream bank simulator.
 *
 * <p>The JSON wire format uses snake_case keys (for example {@code card_number},
 * {@code expiry_date}) as defined by the {@link JsonProperty} annotations on each component.
 * The expiry is serialized as a single {@code expiry_date} string rather than as separate month
 * and year fields.
 *
 * @param cardNumber raw card number forwarded to the bank
 * @param expiryDate card expiry formatted as expected by the downstream bank
 * @param currency ISO 4217 currency code of the payment
 * @param amount payment amount in the currency's minor units
 * @param cvv card verification value
 */
public record BankPaymentRequestRecord(
    @JsonProperty("card_number") String cardNumber,
    @JsonProperty("expiry_date") String expiryDate,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements BankPaymentRequest {

  /**
   * Creates a {@code BankPaymentRequestRecord} after null-checking each component.
   *
   * @param cardNumber raw card number
   * @param expiryDate formatted card expiry string
   * @param currency ISO 4217 currency code
   * @param amount payment amount in minor units
   * @param cvv card verification value
   * @return a new {@code BankPaymentRequestRecord}
   * @throws NullPointerException if {@code cardNumber}, {@code expiryDate}, {@code currency} or
   *     {@code cvv} is {@code null}
   */
  public static BankPaymentRequestRecord of(final String cardNumber,
      final String expiryDate,
      final String currency,
      final long amount,
      final String cvv) {
    requireNonNull(cardNumber, "Card number required");
    requireNonNull(expiryDate, "Expiry date required");
    requireNonNull(currency, "Currency required");
    requireNonNull(cvv, "CVV required");

    return new BankPaymentRequestRecord(cardNumber, expiryDate, currency, amount, cvv);
  }
}