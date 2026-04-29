package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.model.records.PaymentRecord;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(as = PaymentRecord.class)
public interface Payment {

  enum Status {
    AUTHORIZED("Authorized"),
    DECLINED("Declined");

    private final String name;

    Status(final String name) {
      requireNonNull(name, "Name required");
      this.name = name;
    }
  }

  String id();

  Status status();

  String lastFourDigits();

  int expiryMonth();

  int expiryYear();

  String currency();

  long amount();

  static PaymentBuilder builder() {
    return PaymentBuilder.of();
  }
}