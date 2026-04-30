package com.checkout.payment.gateway.model.records;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Deserialize response body returned by the downstream bank simulator.
 *
 * <p>The JSON wire format uses snake_case keys ({@code authorized},
 * {@code authorization_code}) as defined by the {@link JsonProperty} annotations.
 *
 * @param authorized whether the bank authorized the payment
 * @param authorizationCode authorization reference issued by the bank when {@code authorized} is
 *     {@code true}
 */
public record BankPaymentResponseRecord(
    @JsonProperty("authorized") boolean authorized,
    @JsonProperty("authorization_code") String authorizationCode
) {

}