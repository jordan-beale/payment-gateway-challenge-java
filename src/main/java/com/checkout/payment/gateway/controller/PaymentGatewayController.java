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

@RestController("api")
public class PaymentGatewayController {

  public static PaymentGatewayController of(final PaymentGatewayService paymentGatewayService) {
    return new PaymentGatewayController(paymentGatewayService);
  }

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(final PaymentGatewayService paymentGatewayService) {
    requireNonNull(paymentGatewayService, "Payment gateway service required");
    this.paymentGatewayService = paymentGatewayService;
  }

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