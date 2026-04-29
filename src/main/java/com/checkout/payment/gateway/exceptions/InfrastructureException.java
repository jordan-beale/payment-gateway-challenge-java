package com.checkout.payment.gateway.exceptions;

public class InfrastructureException extends RuntimeException {

  public InfrastructureException(final String message) {
    super(message);
  }
}