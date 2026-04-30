package com.checkout.payment.gateway.exceptions;

/**
 * Signals a transient failure when communicating with the downstream acquiring bank. Raised when
 * the bank is unreachable or responds with a 5xx status, indicating the caller should retry the
 * request later.
 * <p>
 * Mapped by {@link com.checkout.payment.gateway.controller.PaymentGatewayController} to HTTP 503
 * {@code SERVICE_UNAVAILABLE}, distinguishing recoverable infrastructure issues from unexpected
 * gateway errors. Extends {@link RuntimeException} so it propagates without checked-exception
 * plumbing.
 */
public class InfrastructureException extends RuntimeException {

  /**
   * Creates a new instance with a detail message describing the infrastructure failure.
   *
   * @param message human-readable description of the failure; conveyed to clients via the error
   *                response body and should be safe to surface
   */
  public InfrastructureException(final String message) {
    super(message);
  }
}