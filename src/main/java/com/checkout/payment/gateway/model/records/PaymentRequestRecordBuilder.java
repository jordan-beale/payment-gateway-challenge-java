package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentRequestBuilder;

public class PaymentRequestRecordBuilder implements PaymentRequestBuilder {

  public static PaymentRequestRecordBuilder of() {
    return new PaymentRequestRecordBuilder();
  }

  private String cardNumber;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private long amount;
  private String cvv;

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

  @Override
  public PaymentRequestBuilder withCardNumber(final String cardNumber) {
    this.cardNumber = requireNonNull(cardNumber, "Card number required");
    return this;
  }

  @Override
  public PaymentRequestBuilder withExpiryMonth(final int expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  @Override
  public PaymentRequestBuilder withExpiryYear(final int expiryYear) {
    this.expiryYear = expiryYear;
    return this;
  }

  @Override
  public PaymentRequestBuilder withCurrency(final String currency) {
    this.currency = requireNonNull(currency, "Currency required");
    return this;
  }

  @Override
  public PaymentRequestBuilder withAmount(final long amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public PaymentRequestBuilder withCvv(final String cvv) {
    this.cvv = requireNonNull(cvv, "CVV required");
    return this;
  }

  public PaymentRequestRecord build() {
    return PaymentRequestRecord.of(requireNonNull(this.cardNumber, "Card number required"),
        this.expiryMonth,
        this.expiryYear,
        requireNonNull(currency, "Currency required"),
        amount,
        requireNonNull(cvv, "CVV required"));
  }
}