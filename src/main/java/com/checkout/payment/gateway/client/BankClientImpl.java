package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exceptions.InfrastructureException;
import com.checkout.payment.gateway.exceptions.UnexpectedException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.Payment.Status;
import com.checkout.payment.gateway.model.records.BankPaymentResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

/**
 * {@link RestTemplate}-backed implementation of {@link BankClient} that POSTs to {@code
 * /payments} on the configured bank URL and maps the response onto a {@link Status}.
 *
 * <p>HTTP failures are translated into domain exceptions: 5xx responses and connectivity issues
 * surface as {@link InfrastructureException} (retryable), while other failures and empty bodies
 * surface as {@link UnexpectedException}.
 */
public class BankClientImpl implements BankClient {

  /**
   * Returns a new {@link BankClientImpl} backed by the supplied collaborators.
   *
   * @param restTemplate {@link RestTemplate} used to perform HTTP calls to the bank
   * @param bankUrl base URL of the acquiring-bank API
   * @return new {@code BankClientImpl} instance
   * @throws NullPointerException if {@code restTemplate} or {@code bankUrl} is {@code null}
   */
  public static BankClientImpl of(final RestTemplate restTemplate,
      final String bankUrl) {
    return new BankClientImpl(restTemplate, bankUrl);
  }

  private static final Logger logger = LoggerFactory.getLogger(BankClientImpl.class);

  private final RestTemplate restTemplate;
  private final String bankUrl;

  /**
   * Creates a new {@code BankClientImpl} bound to the given HTTP client and bank base URL.
   *
   * @param restTemplate {@link RestTemplate} used to perform HTTP calls to the bank
   * @param bankUrl base URL of the acquiring-bank API
   * @throws NullPointerException if {@code restTemplate} or {@code bankUrl} is {@code null}
   */
  public BankClientImpl(final RestTemplate restTemplate,
      final String bankUrl) {
    this.restTemplate = requireNonNull(restTemplate, "Rest template required");
    this.bankUrl = requireNonNull(bankUrl, "Bank URL required");
  }

  /**
   * {@inheritDoc}
   *
   * <p>POSTs the request to {@code <bankUrl>/payments} and inspects the {@code authorized} flag
   * on the response body to derive the {@link Status}.
   *
   * @throws NullPointerException if {@code request} is {@code null}
   * @throws InfrastructureException if the bank responds with a 5xx status or is unreachable
   * @throws UnexpectedException if the bank returns an empty body or any other non-5xx HTTP error
   */
  public Status authorize(final BankPaymentRequest request) {
    requireNonNull(request, "Bank payment request required");
    try {
      final var response = this.restTemplate.postForEntity(bankUrl + "/payments",
          request,
          BankPaymentResponseRecord.class);
      final var body = response.getBody();
      if (body == null) {
        logger.warn("Bank returned empty body");
        throw new UnexpectedException("Bank returned empty body");
      }
      return body.authorized()
          ? Status.AUTHORIZED
          : Status.DECLINED;
    } catch (final HttpStatusCodeException ex) {
      if (ex.getStatusCode().is5xxServerError()) {
        logger.debug("Bank internal failure, retry later", ex);
        throw new InfrastructureException("Bank internal failure, retry later");
      }
      logger.warn("Unexpected exception occurred", ex);
      throw new UnexpectedException("Bank request failed: " + ex.getMessage());
    } catch (final ResourceAccessException ex) {
      logger.debug("Bank unreachable, retry later", ex);
      throw new InfrastructureException("Bank unreachable, retry later");
    }
  }
}