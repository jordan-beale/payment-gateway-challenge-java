package com.checkout.payment.gateway.exceptions;

/**
 * Indicates an unexpected, non-retryable failure while interacting with the downstream acquiring
 * bank. Raised when the bank returns an empty body or a non-5xx error that the gateway cannot
 * reasonably recover from, signaling a likely defect rather than a transient outage.
 * <p>
 * Mapped by {@link com.checkout.payment.gateway.controller.PaymentGatewayController} to HTTP 500
 * {@code INTERNAL_SERVER_ERROR}. Extends {@link RuntimeException} so it propagates without
 * checked-exception plumbing.
 */
public class UnexpectedException extends RuntimeException {

  /**
   * Creates a new instance with a detail message describing the unexpected failure.
   *
   * @param message human-readable description of the failure; conveyed to clients via the error
   *                response body and should be safe to surface
   */
  public UnexpectedException(final String message) {
    super(message);
  }
}