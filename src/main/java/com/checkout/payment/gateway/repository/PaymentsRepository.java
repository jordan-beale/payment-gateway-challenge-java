package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.Payment;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import static java.util.Objects.requireNonNull;

@Repository
public class PaymentsRepository {

  public static PaymentsRepository of() {
    return new PaymentsRepository();
  }

  private final HashMap<String, Payment> payments = new HashMap<>();

  public void store(final Payment payment) {
    requireNonNull(payment, "Payment required");
    this.payments.put(payment.id(), payment);
  }

  public Optional<Payment> paymentById(final String id) {
    requireNonNull(id, "ID required");
    return Optional.ofNullable(this.payments.get(id));
  }
}