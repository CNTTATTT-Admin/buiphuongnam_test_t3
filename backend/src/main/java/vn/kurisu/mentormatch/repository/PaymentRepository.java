package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
