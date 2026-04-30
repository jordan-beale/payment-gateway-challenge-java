package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.Payment;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import static java.util.Objects.requireNonNull;

/**
 * Provides a storage abstraction for persisted {@link Payment} instances keyed by their gateway
 * identifier.
 *
 * <p>
 * The current implementation is backed by an in-memory {@code HashMap}, so stored payments do not
 * survive application restarts and access is not synchronized across threads.
 */
@Repository
public class PaymentsRepository {

  /**
   * Returns a new {@link PaymentsRepository}.
   *
   * @return a freshly constructed repository with no stored payments
   */
  public static PaymentsRepository of() {
    return new PaymentsRepository();
  }

  private final HashMap<String, Payment> payments = new HashMap<>();

  /**
   * Stores the given {@link Payment}, overwriting any existing entry that shares the same
   * {@code id}.
   *
   * @param payment the payment to persist
   * @throws NullPointerException if {@code payment} is {@code null}
   */
  public void store(final Payment payment) {
    requireNonNull(payment, "Payment required");
    this.payments.put(payment.id(), payment);
  }

  /**
   * Looks up a previously stored {@link Payment} by its gateway identifier.
   *
   * @param id the identifier to search for
   * @return an {@code Optional<Payment>} containing the matching payment, or empty when no payment
   *     with the given id exists
   * @throws NullPointerException if {@code id} is {@code null}
   */
  public Optional<Payment> paymentById(final String id) {
    requireNonNull(id, "ID required");
    return Optional.ofNullable(this.payments.get(id));
  }
}