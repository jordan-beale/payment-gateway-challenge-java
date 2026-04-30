package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.Payment.Status;
import com.checkout.payment.gateway.model.records.PaymentRecordBuilder;

/**
 * Fluent builder for {@link Payment}.
 *
 * <p>
 * Validation is performed when {@link #build()} is invoked.
 */
public interface PaymentBuilder {

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype {@link Payment} to copy values from; must not be {@code null}
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  PaymentBuilder usingPrototype(final Payment prototype);

  /**
   * Sets the id.
   *
   * @param id payment identifier
   * @return this builder, for chaining
   */
  PaymentBuilder withId(final String id);

  /**
   * Sets the status.
   *
   * @param status {@link Payment.Status} to record
   * @return this builder, for chaining
   */
  PaymentBuilder withStatus(final Status status);

  /**
   * Sets the last four digits.
   *
   * @param lastFourDigits four-digit numeric string
   * @return this builder, for chaining
   */
  PaymentBuilder withLastFourDigits(final String lastFourDigits);

  /**
   * Sets the expiry month.
   *
   * @param expiryMonth expiry month, expected in the range {@code 1}-{@code 12}
   * @return this builder, for chaining
   */
  PaymentBuilder withExpiryMonth(final int expiryMonth);

  /**
   * Sets the expiry year.
   *
   * @param expiryYear four-digit expiry year
   * @return this builder, for chaining
   */
  PaymentBuilder withExpiryYear(final int expiryYear);

  /**
   * Sets the currency.
   *
   * @param currency ISO 4217 currency code
   * @return this builder, for chaining
   */
  PaymentBuilder withCurrency(final String currency);

  /**
   * Sets the amount.
   *
   * @param amount amount in the currency's minor units
   * @return this builder, for chaining
   */
  PaymentBuilder withAmount(final long amount);

  /**
   * Builds a {@link Payment} from the values accumulated on this builder.
   *
   * @return validated {@code Payment} instance
   * @throws NullPointerException if {@code id}, {@code status}, {@code lastFourDigits} or
   *     {@code currency} has not been set
   * @throws IllegalArgumentException if {@code lastFourDigits} is not exactly four numeric digits,
   *     {@code expiryMonth} is outside {@code 1}-{@code 12}, the card expiry is not in the future,
   *     {@code currency} is not supported, or {@code amount} is not a positive integer
   */
  Payment build();

  /**
   * Returns a new {@link PaymentBuilder}.
   *
   * @return new {@code PaymentBuilder} instance
   */
  static PaymentBuilder of() {
    return PaymentRecordBuilder.of();
  }
}