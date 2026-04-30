package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRequestRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents an inbound payment request submitted to the gateway by a merchant.
 *
 * <p>
 * Carries the raw card data (full PAN, expiry split into month and year, and CVV) that the gateway
 * validates before forwarding a derived {@link BankPaymentRequest} to the downstream bank. Once
 * authorized the request is transformed into a {@link Payment} for persistence.
 */
@JsonDeserialize(as = PaymentRequestRecord.class)
public interface PaymentRequest {

  /**
   * Returns the full primary account number (PAN) of the card to be charged.
   *
   * @return card number as a numeric string of 14-19 digits
   */
  String cardNumber();

  /**
   * Returns the card expiry month as an integer in the range {@code 1}-{@code 12}.
   *
   * @return expiry month
   */
  int expiryMonth();

  /**
   * Returns the four-digit card expiry year.
   *
   * @return expiry year
   */
  int expiryYear();

  /**
   * Returns the ISO 4217 currency code that the payment should be charged in.
   *
   * @return currency code
   */
  String currency();

  /**
   * Returns the amount to charge expressed in the currency's minor units (for example, pence for
   * {@code GBP}).
   *
   * @return amount in minor units
   */
  long amount();

  /**
   * Returns the card verification value supplied by the cardholder.
   *
   * @return CVV as a 3- or 4-digit numeric string
   */
  String cvv();

  /**
   * Returns a fresh {@link PaymentRequestBuilder} for fluently constructing a
   * {@code PaymentRequest}.
   *
   * @return new {@code PaymentRequestBuilder} instance
   */
  static PaymentRequestBuilder builder() {
    return PaymentRequestBuilder.of();
  }
}