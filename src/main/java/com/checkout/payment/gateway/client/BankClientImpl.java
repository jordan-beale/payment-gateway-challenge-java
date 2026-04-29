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

public class BankClientImpl implements BankClient {

  public static BankClientImpl of(final RestTemplate restTemplate,
      final String bankUrl) {
    return new BankClientImpl(restTemplate, bankUrl);
  }

  private static final Logger logger = LoggerFactory.getLogger(BankClientImpl.class);

  private final RestTemplate restTemplate;
  private final String bankUrl;

  public BankClientImpl(final RestTemplate restTemplate,
      final String bankUrl) {
    this.restTemplate = requireNonNull(restTemplate, "Rest template required");
    this.bankUrl = requireNonNull(bankUrl, "Bank URL required");
  }

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