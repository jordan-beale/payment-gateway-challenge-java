package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.BankPaymentRequestRecordBuilder;

/**
 * Fluent builder for {@link BankPaymentRequest}.
 *
 * <p>
 * Validation is performed when {@link #build()} is invoked.
 */
public interface BankPaymentRequestBuilder {

  /**
   * Initializes the builder from an existing instance, translating the inbound representation into
   * the bank wire shape (combining {@code expiryMonth} and {@code expiryYear} into a single
   * {@code MM/YYYY} {@code expiryDate}).
   *
   * @param prototype inbound {@link PaymentRequest} to copy values from; must not be {@code null}
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  BankPaymentRequestBuilder usingPrototype(final PaymentRequest prototype);

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype {@link BankPaymentRequest} to copy values from; must not be {@code null}
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  BankPaymentRequestBuilder usingPrototype(final BankPaymentRequest prototype);

  /**
   * Sets the card number.
   *
   * @param cardNumber full primary account number
   * @return this builder, for chaining
   */
  BankPaymentRequestBuilder withCardNumber(final String cardNumber);

  /**
   * Sets the expiry date.
   *
   * @param expiryDate card expiry formatted as {@code MM/YYYY}
   * @return this builder, for chaining
   */
  BankPaymentRequestBuilder withExpiryDate(final String expiryDate);

  /**
   * Sets the currency.
   *
   * @param currency ISO 4217 currency code
   * @return this builder, for chaining
   */
  BankPaymentRequestBuilder withCurrency(final String currency);

  /**
   * Sets the amount.
   *
   * @param amount amount in the currency's minor units
   * @return this builder, for chaining
   */
  BankPaymentRequestBuilder withAmount(final long amount);

  /**
   * Sets the cvv.
   *
   * @param cvv CVV
   * @return this builder, for chaining
   */
  BankPaymentRequestBuilder withCvv(final String cvv);

  /**
   * Builds a {@link BankPaymentRequest} from the values accumulated on this builder.
   *
   * @return validated {@code BankPaymentRequest} instance ready to be sent downstream
   * @throws NullPointerException if {@code cardNumber}, {@code expiryDate}, {@code currency} or
   *     {@code cvv} has not been set
   */
  BankPaymentRequest build();

  /**
   * Returns a new {@link BankPaymentRequestBuilder}.
   *
   * @return new {@code BankPaymentRequestBuilder} instance
   */
  static BankPaymentRequestBuilder of() {
    return BankPaymentRequestRecordBuilder.of();
  }
}