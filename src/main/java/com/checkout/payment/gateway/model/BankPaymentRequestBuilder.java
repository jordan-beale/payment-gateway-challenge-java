package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.BankPaymentRequestRecordBuilder;

public interface BankPaymentRequestBuilder {

  BankPaymentRequestBuilder usingPrototype(final PaymentRequest prototype);

  BankPaymentRequestBuilder usingPrototype(final BankPaymentRequest prototype);

  BankPaymentRequestBuilder withCardNumber(final String cardNumber);

  BankPaymentRequestBuilder withExpiryDate(final String expiryDate);

  BankPaymentRequestBuilder withCurrency(final String currency);

  BankPaymentRequestBuilder withAmount(final long amount);

  BankPaymentRequestBuilder withCvv(final String cvv);

  BankPaymentRequest build();

  static BankPaymentRequestBuilder of() {
    return BankPaymentRequestRecordBuilder.of();
  }
}