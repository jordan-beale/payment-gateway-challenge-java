package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static java.util.Objects.requireNonNull;

/**
 * Represents a payment that has been processed by the gateway and is suitable for persistence or
 * outbound responses.
 *
 * <p>
 * Unlike {@link PaymentRequest}, which carries the raw card data submitted by a merchant, a
 * {@code Payment} stores only the masked {@code lastFourDigits} of the card together with the
 * authorization outcome captured in {@link Status}.
 */
@JsonDeserialize(as = PaymentRecord.class)
public interface Payment {

  /**
   * Authorisation outcome attached to a {@link Payment} after the downstream bank has responded.
   */
  enum Status {
    /** Indicates the bank authorized the payment. */
    AUTHORIZED("Authorized"),
    /** Indicates the bank declined the payment. */
    DECLINED("Declined");

    private final String name;

    Status(final String name) {
      requireNonNull(name, "Name required");
      this.name = name;
    }
  }

  /**
   * Returns the unique identifier assigned by the gateway when the payment is persisted.
   *
   * @return gateway-assigned payment identifier
   */
  String id();

  /**
   * Returns the authorization outcome reported by the downstream bank.
   *
   * @return {@link Status} of the payment
   */
  Status status();

  /**
   * Returns the last four digits of the card used for the payment, retained in place of the full
   * PAN for storage and display.
   *
   * @return four-digit numeric string masking the card number
   */
  String lastFourDigits();

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
   * Returns the ISO 4217 currency code that the payment was authorized in.
   *
   * @return currency code
   */
  String currency();

  /**
   * Returns the authorized amount expressed in the currency's minor units (for example, pence for
   * {@code GBP}).
   *
   * @return amount in minor units
   */
  long amount();

  /**
   * Returns a fresh {@link PaymentBuilder} for fluently constructing a {@code Payment}.
   *
   * @return new {@code PaymentBuilder} instance
   */
  static PaymentBuilder builder() {
    return PaymentBuilder.of();
  }
}