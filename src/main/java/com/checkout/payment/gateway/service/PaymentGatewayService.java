package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.BankClient;
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

@Service
public class PaymentGatewayService {

  public static PaymentGatewayService of(final PaymentsRepository paymentsRepository,
      final BankClient bankClient) {
    return new PaymentGatewayService(paymentsRepository, bankClient);
  }

  private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankClient bankClient;

  public PaymentGatewayService(final PaymentsRepository paymentsRepository,
      final BankClient bankClient) {
    this.paymentsRepository = requireNonNull(paymentsRepository, "Payments repository required");
    this.bankClient = requireNonNull(bankClient, "Bank client required");
  }

  public Payment paymentById(final String id) {
    requireNonNull(id, "ID required");
    logger.debug("Fetching payment with ID [{}]", id);
    return this.paymentsRepository.paymentById(id)
        .orElseThrow(() -> new NoSuchElementException("No payment with provided ID"));
  }

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