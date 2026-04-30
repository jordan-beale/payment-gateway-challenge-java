package com.checkout.payment.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the payment gateway service.
 *
 * <p>
 * Bootstraps the application context and triggers component scanning and auto-configuration via
 * {@link SpringBootApplication}.
 */
@SpringBootApplication
public class PaymentGatewayApplication {

  /**
   * Launches the payment gateway Spring Boot application.
   *
   * @param args command-line arguments forwarded to {@link SpringApplication#run}.
   */
  public static void main(final String[] args) {
    SpringApplication.run(PaymentGatewayApplication.class, args);
  }
}