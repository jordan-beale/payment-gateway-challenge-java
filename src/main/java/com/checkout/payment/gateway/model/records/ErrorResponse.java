package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

public record ErrorResponse(String message) {

  public static ErrorResponse of(final String message) {
    return new ErrorResponse(message);
  }

  public ErrorResponse {
    requireNonNull(message, "Message required");
  }
}