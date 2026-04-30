package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.exceptions.InfrastructureException;
import com.checkout.payment.gateway.exceptions.UnexpectedException;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.records.ErrorResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * Exposes the public HTTP surface of the payment gateway, delegating all business logic to
 * {@link PaymentGatewayService}.
 * <p>
 * Provides a {@code GET /payment/{id}} endpoint for retrieving a previously stored payment and a
 * {@code POST /payment} endpoint for processing a new {@link PaymentRequest}. Service-layer
 * exceptions are translated into {@link ErrorResponse} bodies with appropriate HTTP status codes.
 */
@RestController("api")
public class PaymentGatewayController {

  /**
   * Returns a new {@link PaymentGatewayController}.
   *
   * @param paymentGatewayService the service used to fetch and process payments
   * @return a fully constructed controller instance
   */
  public static PaymentGatewayController of(final PaymentGatewayService paymentGatewayService) {
    return new PaymentGatewayController(paymentGatewayService);
  }

  private final PaymentGatewayService paymentGatewayService;

  /**
   * Constructs a controller backed by the given service.
   *
   * @param paymentGatewayService the service used to fetch and process payments
   * @throws NullPointerException if {@code paymentGatewayService} is {@code null}
   */
  public PaymentGatewayController(final PaymentGatewayService paymentGatewayService) {
    requireNonNull(paymentGatewayService, "Payment gateway service required");
    this.paymentGatewayService = paymentGatewayService;
  }

  /**
   * Handles {@code GET /payment/{id}} and returns the stored payment for the given identifier.
   *
   * @param id the payment identifier extracted from the request path
   * @return a {@link ResponseEntity} with status {@code 200} and the matching payment as the body
   *         on success
   *         <p>
   *         Error mapping:
   *         <ul>
   *           <li>{@link NoSuchElementException} (no payment with the provided id) maps to
   *               {@code 404 NOT_FOUND} with an {@link ErrorResponse} body.</li>
   *           <li>{@link NullPointerException} (raised when {@code id} is {@code null}) maps to
   *               {@code 422 UNPROCESSABLE_ENTITY} with an {@link ErrorResponse} body.</li>
   *         </ul>
   */
  @GetMapping("/payment/{id}")
  public ResponseEntity<?> paymentById(@PathVariable final String id) {
    try {
      return new ResponseEntity<>(this.paymentGatewayService.paymentById(id), HttpStatus.OK);
    } catch (final NoSuchElementException ex) {
      return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()), HttpStatus.NOT_FOUND);
    } catch (final NullPointerException ex) {
      return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()),
          HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  /**
   * Handles {@code POST /payment} by processing the supplied request through the gateway service.
   *
   * @param paymentRequest the deserialized JSON request body describing the payment to authorize
   * @return a {@link ResponseEntity} with status {@code 201} and the resulting
   *         {@link com.checkout.payment.gateway.model.Payment} as the body on success
   *         <p>
   *         Error mapping:
   *         <ul>
   *           <li>{@link IllegalArgumentException} or {@link NullPointerException} (validation
   *               failures or a missing request) map to {@code 422 UNPROCESSABLE_ENTITY} with an
   *               {@link ErrorResponse} body.</li>
   *           <li>{@link UnexpectedException} (non-recoverable downstream failure) maps to
   *               {@code 500 INTERNAL_SERVER_ERROR} with an {@link ErrorResponse} body.</li>
   *           <li>{@link InfrastructureException} (transient acquiring-bank outage) maps to
   *               {@code 503 SERVICE_UNAVAILABLE} with an {@link ErrorResponse} body.</li>
   *         </ul>
   */
  @PostMapping("/payment")
  public ResponseEntity<?> processPayment(@RequestBody final PaymentRequest paymentRequest) {
    try {
      return new ResponseEntity<>(this.paymentGatewayService.processPayment(paymentRequest),
          HttpStatus.CREATED);
    } catch (final IllegalArgumentException | NullPointerException ex) {
      return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()),
          HttpStatus.UNPROCESSABLE_ENTITY);
    } catch (final UnexpectedException ex) {
      return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (final InfrastructureException ex) {
      return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()),
          HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}