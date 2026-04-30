package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.BankClient;
import com.checkout.payment.gateway.exceptions.InfrastructureException;
import com.checkout.payment.gateway.exceptions.UnexpectedException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentRequestBuilder;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

/**
 * Orchestrates payment authorization by validating inbound requests, delegating the authorization
 * decision to the acquiring {@link BankClient}, and persisting the resulting {@link Payment}.
 *
 * <p>Acts as the single entry point for the gateway flow, shielding callers from direct interaction
 * with the bank and the {@link PaymentsRepository}.
 */
@Service
public class PaymentGatewayService {

  /**
   * Returns a new {@link PaymentGatewayService}.
   *
   * @param paymentsRepository repository used to persist and retrieve {@link Payment} records
   * @param bankClient client used to obtain authorization decisions from the acquiring bank
   * @return new {@code PaymentGatewayService} instance
   * @throws NullPointerException if any argument is {@code null}
   */
  public static PaymentGatewayService of(final PaymentsRepository paymentsRepository,
      final BankClient bankClient) {
    return new PaymentGatewayService(paymentsRepository, bankClient);
  }

  private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankClient bankClient;

  /**
   * Creates a new service bound to the supplied repository and bank client.
   *
   * @param paymentsRepository repository used to persist and retrieve {@link Payment} records
   * @param bankClient client used to obtain authorization decisions from the acquiring bank
   * @throws NullPointerException if any argument is {@code null}
   */
  public PaymentGatewayService(final PaymentsRepository paymentsRepository,
      final BankClient bankClient) {
    this.paymentsRepository = requireNonNull(paymentsRepository, "Payments repository required");
    this.bankClient = requireNonNull(bankClient, "Bank client required");
  }

  /**
   * Retrieves a previously stored {@link Payment} by its identifier.
   *
   * @param id identifier of the payment to fetch
   * @return the stored {@link Payment} associated with {@code id}
   * @throws NullPointerException if {@code id} is {@code null}
   * @throws NoSuchElementException if no payment with the given identifier exists
   */
  public Payment paymentById(final String id) {
    requireNonNull(id, "ID required");
    logger.debug("Fetching payment with ID [{}]", id);
    return this.paymentsRepository.paymentById(id)
        .orElseThrow(() -> new NoSuchElementException("No payment with provided ID"));
  }

  /**
   * Validates the inbound {@link PaymentRequest}, forwards a {@link BankPaymentRequest} to the
   * {@link BankClient} for authorisation, and persists the resulting {@link Payment}.
   *
   * <p>The stored payment is assigned a freshly generated {@link UUID} identifier and the card
   * number is masked so that only the last four digits are retained. The status reflects the
   * decision returned by the bank.
   *
   * @param paymentRequest inbound payment request to authorize and store
   * @return the persisted {@link Payment} including its generated id, masked PAN, and bank status
   * @throws NullPointerException if {@code paymentRequest} is {@code null}
   * @throws IllegalArgumentException if the request fails revalidation by the builder
   * @throws InfrastructureException if the bank is unreachable or returns a 5xx response
   * @throws UnexpectedException if the bank returns an empty body or any other non-5xx HTTP error
   */
  public Payment processPayment(final PaymentRequest paymentRequest) {
    requireNonNull(paymentRequest, "Payment request required");

    final var paymentRequestValidated = PaymentRequestBuilder.of()
        .usingPrototype(paymentRequest)
        .build();

    final var bankRequest = BankPaymentRequest.builder()
        .usingPrototype(paymentRequestValidated)
        .build();

    final var status = this.bankClient.authorize(bankRequest);

    final var cardNumber = paymentRequestValidated.cardNumber();
    final var payment = Payment.builder()
        .withId(UUID.randomUUID().toString())
        .withStatus(status)
        .withLastFourDigits(cardNumber.substring(cardNumber.length() - 4))
        .withExpiryMonth(paymentRequestValidated.expiryMonth())
        .withExpiryYear(paymentRequestValidated.expiryYear())
        .withCurrency(paymentRequestValidated.currency())
        .withAmount(paymentRequestValidated.amount())
        .build();

    this.paymentsRepository.store(payment);
    return payment;
  }
}