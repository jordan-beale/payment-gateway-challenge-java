package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentRequestBuilder;

/**
 * Fluent builder for {@link PaymentRequestRecord}.
 */
public class PaymentRequestRecordBuilder implements PaymentRequestBuilder {

  /**
   * Returns a new builder.
   *
   * @return a fresh {@code PaymentRequestRecordBuilder}
   */
  public static PaymentRequestRecordBuilder of() {
    return new PaymentRequestRecordBuilder();
  }

  private String cardNumber;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private long amount;
  private String cvv;

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype existing {@link PaymentRequest} whose components are copied into this builder
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  @Override
  public PaymentRequestBuilder usingPrototype(final PaymentRequest prototype) {
    requireNonNull(prototype, "Payment request prototype required");

    withCardNumber(prototype.cardNumber());
    withExpiryMonth(prototype.expiryMonth());
    withExpiryYear(prototype.expiryYear());
    withCurrency(prototype.currency());
    withAmount(prototype.amount());
    withCvv(prototype.cvv());

    return this;
  }

  /**
   * Sets the card number.
   *
   * @param cardNumber raw card number
   * @return this builder, for chaining
   * @throws NullPointerException if {@code cardNumber} is {@code null}
   */
  @Override
  public PaymentRequestBuilder withCardNumber(final String cardNumber) {
    this.cardNumber = requireNonNull(cardNumber, "Card number required");
    return this;
  }

  /**
   * Sets the expiry month.
   *
   * @param expiryMonth card expiry month
   * @return this builder, for chaining
   */
  @Override
  public PaymentRequestBuilder withExpiryMonth(final int expiryMonth) {
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
  public PaymentRequestBuilder withExpiryYear(final int expiryYear) {
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
  public PaymentRequestBuilder withCurrency(final String currency) {
    this.currency = requireNonNull(currency, "Currency required");
    return this;
  }

  /**
   * Sets the amount.
   *
   * @param amount requested payment amount in the currency's minor units
   * @return this builder, for chaining
   */
  @Override
  public PaymentRequestBuilder withAmount(final long amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Sets the cvv.
   *
   * @param cvv card verification value
   * @return this builder, for chaining
   * @throws NullPointerException if {@code cvv} is {@code null}
   */
  @Override
  public PaymentRequestBuilder withCvv(final String cvv) {
    this.cvv = requireNonNull(cvv, "CVV required");
    return this;
  }

  /**
   * Builds a validated {@link PaymentRequestRecord} from the configured components.
   *
   * @return a new {@link PaymentRequestRecord}
   * @throws NullPointerException if {@code cardNumber}, {@code currency} or {@code cvv} has not
   *     been set
   * @throws IllegalArgumentException if any value violates the validation rules enforced by
   *     {@link PaymentRequestRecord#of(String, int, int, String, long, String)}
   */
  public PaymentRequestRecord build() {
    return PaymentRequestRecord.of(requireNonNull(this.cardNumber, "Card number required"),
        this.expiryMonth,
        this.expiryYear,
        requireNonNull(currency, "Currency required"),
        amount,
        requireNonNull(cvv, "CVV required"));
  }
}