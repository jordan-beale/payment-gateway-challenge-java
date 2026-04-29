package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRequestRecordBuilder;

public interface PaymentRequestBuilder {

  PaymentRequestBuilder usingPrototype(final PaymentRequest prototype);

  PaymentRequestBuilder withCardNumber(final String cardNumber);

  PaymentRequestBuilder withExpiryMonth(final int expiryMonth);

  PaymentRequestBuilder withExpiryYear(final int expiryYear);

  PaymentRequestBuilder withCurrency(final String currency);

  PaymentRequestBuilder withAmount(final long amount);

  PaymentRequestBuilder withCvv(final String cvv);

  PaymentRequest build();

  static PaymentRequestBuilder of() {
    return PaymentRequestRecordBuilder.of();
  }
}