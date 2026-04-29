package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.Payment.Status;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

public interface BankClient {

  Status authorize(final BankPaymentRequest request);

  static BankClient of(final RestTemplate restTemplate,
      final String bankUrl) {
    requireNonNull(restTemplate, "Rest template required");
    requireNonNull(bankUrl, "Bank URL required");
    return BankClientImpl.of(restTemplate, bankUrl);
  }
}