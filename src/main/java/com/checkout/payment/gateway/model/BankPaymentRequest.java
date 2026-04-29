package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.BankPaymentRequestRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = BankPaymentRequestRecord.class)
public interface BankPaymentRequest {

  String cardNumber();

  String expiryDate();

  String currency();

  long amount();

  String cvv();

  static BankPaymentRequestBuilder builder() {
    return BankPaymentRequestBuilder.of();
  }
}