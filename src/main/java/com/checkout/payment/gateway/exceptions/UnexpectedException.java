package com.checkout.payment.gateway.exceptions;

public class UnexpectedException extends RuntimeException {

  public UnexpectedException(final String message) {
    super(message);
  }
}