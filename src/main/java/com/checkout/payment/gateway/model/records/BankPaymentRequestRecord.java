package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public record BankPaymentRequestRecord(
    @JsonProperty("card_number") String cardNumber,
    @JsonProperty("expiry_date") String expiryDate,
    @JsonProperty("currency") String currency,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements BankPaymentRequest {

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