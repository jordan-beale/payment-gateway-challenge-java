package com.checkout.payment.gateway.model.records;

import static java.util.Objects.requireNonNull;

/**
 * JSON error body shape returned by {@code PaymentGatewayController} for non-2xx responses.
 *
 * @param message human-readable description of the error condition
 */
public record ErrorResponse(String message) {

  /**
   * Creates an {@code ErrorResponse} carrying the supplied message.
   *
   * @param message human-readable description of the error
   * @return a new {@code ErrorResponse}
   * @throws NullPointerException if {@code message} is {@code null}
   */
  public static ErrorResponse of(final String message) {
    return new ErrorResponse(message);
  }

  /**
   * Compact constructor that null-checks the message.
   *
   * @throws NullPointerException if {@code message} is {@code null}
   */
  public ErrorResponse {
    requireNonNull(message, "Message required");
  }
}