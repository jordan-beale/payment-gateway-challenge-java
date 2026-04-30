package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentRequestBuilder;
import com.checkout.payment.gateway.model.PaymentRequest;

/**
 * Fluent builder for {@link BankPaymentRequestRecord}.
 */
public class BankPaymentRequestRecordBuilder implements BankPaymentRequestBuilder {

  /**
   * Returns a new builder.
   *
   * @return a fresh {@code BankPaymentRequestRecordBuilder}
   */
  public static BankPaymentRequestRecordBuilder of() {
    return new BankPaymentRequestRecordBuilder();
  }

  private String cardNumber;
  private String expiryDate;
  private String currency;
  private long amount;
  private String cvv;

  /**
   * Initializes the builder from an existing instance.
   *
   * <p>The {@code expiryMonth} and {@code expiryYear} of the prototype are combined into the
   * downstream {@code expiryDate} string in {@code M/yyyy} form.
   *
   * @param prototype existing {@link PaymentRequest} whose components are copied into this builder
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  @Override
  public BankPaymentRequestBuilder usingPrototype(final PaymentRequest prototype) {
    requireNonNull(prototype, "Payment request prototype required");

    withCardNumber(prototype.cardNumber());
    withExpiryDate("%d/%d".formatted(prototype.expiryMonth(), prototype.expiryYear()));
    withCurrency(prototype.currency());
    withAmount(prototype.amount());
    withCvv(prototype.cvv());

    return this;
  }

  /**
   * Initializes the builder from an existing instance.
   *
   * @param prototype existing {@link BankPaymentRequest} whose components are copied into this
   *     builder
   * @return this builder, for chaining
   * @throws NullPointerException if {@code prototype} is {@code null}
   */
  @Override
  public BankPaymentRequestBuilder usingPrototype(final BankPaymentRequest prototype) {
    requireNonNull(prototype, "Payment request prototype required");

    withCardNumber(prototype.cardNumber());
    withExpiryDate(prototype.expiryDate());
    withCurrency(prototype.currency());
    withAmount(prototype.amount());
    withCvv(prototype.cvv());

    return this;
  }

  /**
   * Sets the card number.
   *
   * @param cardNumber raw card number to forward to the bank
   * @return this builder, for chaining
   * @throws NullPointerException if {@code cardNumber} is {@code null}
   */
  @Override
  public BankPaymentRequestBuilder withCardNumber(final String cardNumber) {
    this.cardNumber = requireNonNull(cardNumber, "Card number required");
    return this;
  }

  /**
   * Sets the expiry date.
   *
   * @param expiryDate formatted card expiry string as expected by the downstream bank
   * @return this builder, for chaining
   */
  @Override
  public BankPaymentRequestBuilder withExpiryDate(final String expiryDate) {
    this.expiryDate = expiryDate;
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
  public BankPaymentRequestBuilder withCurrency(final String currency) {
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
  public BankPaymentRequestBuilder withAmount(final long amount) {
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
  public BankPaymentRequestBuilder withCvv(final String cvv) {
    this.cvv = requireNonNull(cvv, "CVV required");
    return this;
  }

  /**
   * Builds a {@link BankPaymentRequestRecord} from the configured components.
   *
   * @return a new {@link BankPaymentRequestRecord}
   * @throws NullPointerException if {@code cardNumber}, {@code expiryDate}, {@code currency} or
   *     {@code cvv} has not been set
   */
  public BankPaymentRequestRecord build() {
    return BankPaymentRequestRecord.of(requireNonNull(this.cardNumber, "Card number required"),
        requireNonNull(this.expiryDate, "Expiry date required"),
        requireNonNull(currency, "Currency required"),
        amount,
        requireNonNull(cvv, "CVV required"));
  }
}