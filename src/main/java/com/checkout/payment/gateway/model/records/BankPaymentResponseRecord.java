package com.checkout.payment.gateway.model.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankPaymentResponseRecord(
    @JsonProperty("authorized") boolean authorized,
    @JsonProperty("authorization_code") String authorizationCode
) {

}