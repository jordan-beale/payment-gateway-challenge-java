package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRequestRecordBuilder;

/**
 * Fluent builder for {@link PaymentRequest}.
 *
 * <p>
 * Validation is performed when {@link #build()} is invoked.
 */
public interface PaymentRequestBuilder {

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype {@link PaymentRequest} to copy values from; must not be {@code null}
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  PaymentRequestBuilder usingPrototype(final PaymentRequest prototype);

  /**
   * Sets the card number.
   *
   * @param cardNumber full primary account number
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withCardNumber(final String cardNumber);

  /**
   * Sets the expiry month.
   *
   * @param expiryMonth expiry month, expected in the range {@code 1}-{@code 12}
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withExpiryMonth(final int expiryMonth);

  /**
   * Sets the expiry year.
   *
   * @param expiryYear four-digit expiry year
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withExpiryYear(final int expiryYear);

  /**
   * Sets the currency.
   *
   * @param currency ISO 4217 currency code
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withCurrency(final String currency);

  /**
   * Sets the amount.
   *
   * @param amount amount in the currency's minor units
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withAmount(final long amount);

  /**
   * Sets the cvv.
   *
   * @param cvv CVV as a 3- or 4-digit numeric string
   * @return this builder, for chaining
   */
  PaymentRequestBuilder withCvv(final String cvv);

  /**
   * Builds a {@link PaymentRequest} from the values accumulated on this builder.
   *
   * @return validated {@code PaymentRequest} instance
   * @throws NullPointerException if {@code cardNumber}, {@code currency} or {@code cvv} has not
   *     been set
   * @throws IllegalArgumentException if {@code cardNumber} is not 14-19 numeric digits,
   *     {@code expiryMonth} is outside {@code 1}-{@code 12}, the card expiry is not in the future,
   *     {@code currency} is not supported, {@code amount} is not a positive integer, or
   *     {@code cvv} is not 3-4 numeric digits
   */
  PaymentRequest build();

  /**
   * Returns a new {@link PaymentRequestBuilder}.
   *
   * @return new {@code PaymentRequestBuilder} instance
   */
  static PaymentRequestBuilder of() {
    return PaymentRequestRecordBuilder.of();
  }
}