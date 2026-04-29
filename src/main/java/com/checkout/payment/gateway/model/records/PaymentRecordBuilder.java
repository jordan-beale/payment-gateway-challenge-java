package com.checkout.payment.gateway.model.records;

import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.Payment.Status;
import com.checkout.payment.gateway.model.PaymentBuilder;

import static java.util.Objects.requireNonNull;

public class PaymentRecordBuilder implements PaymentBuilder {

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

  @Override
  public PaymentBuilder withId(final String id) {
    this.id = requireNonNull(id, "ID required");
    return this;
  }

  @Override
  public PaymentBuilder withStatus(final Status status) {
    this.status = requireNonNull(status, "Status required");
    return this;
  }

  @Override
  public PaymentBuilder withLastFourDigits(final String lastFourDigits) {
    this.lastFourDigits = requireNonNull(lastFourDigits, "Last four digits required");
    return this;
  }

  @Override
  public PaymentBuilder withExpiryMonth(final int expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  @Override
  public PaymentBuilder withExpiryYear(final int expiryYear) {
    this.expiryYear = expiryYear;
    return this;
  }

  @Override
  public PaymentBuilder withCurrency(final String currency) {
    this.currency = requireNonNull(currency, "Currency required");
    return this;
  }

  @Override
  public PaymentBuilder withAmount(final long amount) {
    this.amount = amount;
    return this;
  }

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