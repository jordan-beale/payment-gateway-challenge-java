package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.Payment.Status;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

/**
 * Abstraction over the downstream acquiring-bank HTTP API used by the payment gateway to obtain an
 * authorization decision for a card payment.
 *
 * <p>Implementations translate a {@link BankPaymentRequest} into a call to the bank and map the
 * response onto a {@link Status} of {@code AUTHORIZED} or {@code DECLINED}.
 */
public interface BankClient {

  /**
   * Submits a payment to the acquiring bank and returns the resulting authorization decision.
   *
   * @param request bank payment request describing the card and amount to authorize
   * @return {@link Status#AUTHORIZED} if the bank approved the payment, otherwise
   *         {@link Status#DECLINED}
   * @throws NullPointerException if {@code request} is {@code null}
   */
  Status authorize(final BankPaymentRequest request);

  /**
   * Returns a new {@link BankClient} backed by the supplied collaborators.
   *
   * @param restTemplate {@link RestTemplate} used to perform HTTP calls to the bank
   * @param bankUrl base URL of the acquiring-bank API
   * @return new {@code BankClient} instance
   * @throws NullPointerException if {@code restTemplate} or {@code bankUrl} is {@code null}
   */
  static BankClient of(final RestTemplate restTemplate,
      final String bankUrl) {
    requireNonNull(restTemplate, "Rest template required");
    requireNonNull(bankUrl, "Bank URL required");
    return BankClientImpl.of(restTemplate, bankUrl);
  }
}