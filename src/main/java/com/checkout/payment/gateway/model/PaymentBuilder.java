package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.Payment.Status;
import com.checkout.payment.gateway.model.records.PaymentRecordBuilder;

public interface PaymentBuilder {

  PaymentBuilder usingPrototype(final Payment prototype);

  PaymentBuilder withId(final String id);

  PaymentBuilder withStatus(final Status status);

  PaymentBuilder withLastFourDigits(final String lastFourDigits);

  PaymentBuilder withExpiryMonth(final int expiryMonth);

  PaymentBuilder withExpiryYear(final int expiryYear);

  PaymentBuilder withCurrency(final String currency);

  PaymentBuilder withAmount(final long amount);

  Payment build();

  static PaymentBuilder of() {
    return PaymentRecordBuilder.of();
  }
}