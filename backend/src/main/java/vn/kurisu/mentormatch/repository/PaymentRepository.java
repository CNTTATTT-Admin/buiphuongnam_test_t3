package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.Payment;

import vn.kurisu.mentormatch.entity.PaymentStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByBookingIdAndStatus(Integer bookingId, PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT YEAR(p.paidAt), MONTH(p.paidAt), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.status = :status AND p.paidAt IS NOT NULL " +
           "GROUP BY YEAR(p.paidAt), MONTH(p.paidAt) ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)")
    List<Object[]> findMonthlyRevenueByStatus(@Param("status") PaymentStatus status);
}

