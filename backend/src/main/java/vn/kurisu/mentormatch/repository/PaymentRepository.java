package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Payment;

import vn.kurisu.mentormatch.entity.PaymentStatus;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByBookingIdAndStatus(Integer bookingId, PaymentStatus status);
}
