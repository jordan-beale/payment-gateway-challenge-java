package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentRequestBuilder;
import com.checkout.payment.gateway.model.PaymentRequest;

public class BankPaymentRequestRecordBuilder implements BankPaymentRequestBuilder {

  public static BankPaymentRequestRecordBuilder of() {
    return new BankPaymentRequestRecordBuilder();
  }

  private String cardNumber;
  private String expiryDate;
  private String currency;
  private long amount;
  private String cvv;

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

  @Override
  public BankPaymentRequestBuilder withCardNumber(final String cardNumber) {
    this.cardNumber = requireNonNull(cardNumber, "Card number required");
    return this;
  }

  @Override
  public BankPaymentRequestBuilder withExpiryDate(final String expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  @Override
  public BankPaymentRequestBuilder withCurrency(final String currency) {
    this.currency = requireNonNull(currency, "Currency required");
    return this;
  }

  @Override
  public BankPaymentRequestBuilder withAmount(final long amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public BankPaymentRequestBuilder withCvv(final String cvv) {
    this.cvv = requireNonNull(cvv, "CVV required");
    return this;
  }

  public BankPaymentRequestRecord build() {
    return BankPaymentRequestRecord.of(requireNonNull(this.cardNumber, "Card number required"),
        requireNonNull(this.expiryDate, "Expiry date required"),
        requireNonNull(currency, "Currency required"),
        amount,
        requireNonNull(cvv, "CVV required"));
  }
}