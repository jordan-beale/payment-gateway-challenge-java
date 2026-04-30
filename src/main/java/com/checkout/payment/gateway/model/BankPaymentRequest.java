package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.BankPaymentRequestRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents the request payload that the gateway sends to the downstream bank in order to obtain
 * an authorization decision.
 *
 * <p>
 * Differs from {@link PaymentRequest} in the wire shape expected by the bank: the expiry is
 * combined into a single {@code MM/YYYY} string rather than separate month and year fields, and
 * the amount is the value in the currency's minor units that the bank should authorize.
 */
@JsonDeserialize(as = BankPaymentRequestRecord.class)
public interface BankPaymentRequest {

  /**
   * Returns the full primary account number (PAN) of the card to be charged.
   *
   * @return card number
   */
  String cardNumber();

  /**
   * Returns the card expiry rendered as a {@code MM/YYYY} string for the bank.
   *
   * @return expiry date string
   */
  String expiryDate();

  /**
   * Returns the ISO 4217 currency code that the bank should authorize in.
   *
   * @return currency code
   */
  String currency();

  /**
   * Returns the amount to authorize expressed in the currency's minor units.
   *
   * @return amount in minor units
   */
  long amount();

  /**
   * Returns the card verification value forwarded to the bank.
   *
   * @return CVV
   */
  String cvv();

  /**
   * Returns a fresh {@link BankPaymentRequestBuilder} for fluently constructing a
   * {@code BankPaymentRequest}.
   *
   * @return new {@code BankPaymentRequestBuilder} instance
   */
  static BankPaymentRequestBuilder builder() {
    return BankPaymentRequestBuilder.of();
  }
}