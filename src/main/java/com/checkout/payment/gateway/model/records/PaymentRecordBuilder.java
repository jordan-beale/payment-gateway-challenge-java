package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.Payment.Status;
import com.checkout.payment.gateway.model.PaymentBuilder;

import static java.util.Objects.requireNonNull;

/**
 * Fluent builder for {@link PaymentRecord}.
 */
public class PaymentRecordBuilder implements PaymentBuilder {

  /**
   * Returns a new {@link PaymentRecordBuilder}.
   *
   * @return new {@code PaymentRecordBuilder} instance
   */
  public static PaymentRecordBuilder of() {
    return new PaymentRecordBuilder();
  }

  private String id;
  private Status status;
  private String lastFourDigits;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private long amount;

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype existing {@link Payment} whose components are copied into this builder
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  @Override
  public PaymentBuilder usingPrototype(final Payment prototype) {
    requireNonNull(prototype, "Payment prototype required");

    withId(prototype.id());
    withStatus(prototype.status());
    withLastFourDigits(prototype.lastFourDigits());
    withExpiryMonth(prototype.expiryMonth());
    withExpiryYear(prototype.expiryYear());
    withCurrency(prototype.currency());
    withAmount(prototype.amount());

    return this;
  }

  /**
   * Sets the id.
   *
   * @param id unique identifier for the payment
   * @return this builder, for chaining
   * @throws NullPointerException if {@code id} is {@code null}
   */
  @Override
  public PaymentBuilder withId(final String id) {
    this.id = requireNonNull(id, "ID required");
    return this;
  }

  /**
   * Sets the status.
   *
   * @param status authorization outcome
   * @return this builder, for chaining
   * @throws NullPointerException if {@code status} is {@code null}
   */
  @Override
  public PaymentBuilder withStatus(final Status status) {
    this.status = requireNonNull(status, "Status required");
    return this;
  }

  /**
   * Sets the last four digits.
   *
   * @param lastFourDigits last four digits of the card number
   * @return this builder, for chaining
   * @throws NullPointerException if {@code lastFourDigits} is {@code null}
   */
  @Override
  public PaymentBuilder withLastFourDigits(final String lastFourDigits) {
    this.lastFourDigits = requireNonNull(lastFourDigits, "Last four digits required");
    return this;
  }

  /**
   * Sets the expiry month.
   *
   * @param expiryMonth card expiry month
   * @return this builder, for chaining
   */
  @Override
  public PaymentBuilder withExpiryMonth(final int expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  /**
   * Sets the expiry year.
   *
   * @param expiryYear four-digit card expiry year
   * @return this builder, for chaining
   */
  @Override
  public PaymentBuilder withExpiryYear(final int expiryYear) {
    this.expiryYear = expiryYear;
    return this;
  }

  /**
   * Sets the currency.
   *
   * @param currency ISO 4217 currency code
   * @return this builder, for chaining
   * @throws NullPointerException if {@code currency} is {@code null}
   */
  @Override
  public PaymentBuilder withCurrency(final String currency) {
    this.currency = requireNonNull(currency, "Currency required");
    return this;
  }

  /**
   * Sets the amount.
   *
   * @param amount payment amount in the currency's minor units
   * @return this builder, for chaining
   */
  @Override
  public PaymentBuilder withAmount(final long amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Builds a validated {@link PaymentRecord} from the configured components.
   *
   * @return new {@code PaymentRecord} instance
   * @throws NullPointerException if {@code id}, {@code status}, {@code lastFourDigits} or
   *     {@code currency} has not been set
   * @throws IllegalArgumentException if any value violates the validation rules enforced by
   *     {@link PaymentRecord#of(String, Status, String, int, int, String, long)}
   */
  public PaymentRecord build() {
    return PaymentRecord.of(requireNonNull(this.id, "ID required"),
        requireNonNull(this.status, "Status required"),
        requireNonNull(this.lastFourDigits, "Last four digits required"),
        this.expiryMonth,
        this.expiryYear,
        requireNonNull(currency, "Currency required"),
        amount);
  }
}