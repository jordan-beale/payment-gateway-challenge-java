package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRequestRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PaymentRequestRecord.class)
public interface PaymentRequest {

  String cardNumber();

  int expiryMonth();

  int expiryYear();

  String currency();

  long amount();

  String cvv();

  static PaymentRequestBuilder builder() {
    return PaymentRequestBuilder.of();
  }
}