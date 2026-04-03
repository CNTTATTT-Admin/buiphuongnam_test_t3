package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kurisu.mentormatch.dto.response.AdminDashboardStats;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.entity.BookingStatus;
import vn.kurisu.mentormatch.entity.PaymentStatus;
import vn.kurisu.mentormatch.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final DisputeRepository disputeRepository;

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.05"); // 5%

    @GetMapping("/stats")
    public ApiResponse<AdminDashboardStats> getDashboardStats() {
        // Total revenue from successful payments
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS);
        BigDecimal systemRevenue = totalRevenue.multiply(COMMISSION_RATE).setScale(0, RoundingMode.HALF_UP);

        // Monthly breakdown
        List<Object[]> monthlyData = paymentRepository.findMonthlyRevenueByStatus(PaymentStatus.SUCCESS);
        List<AdminDashboardStats.MonthlyRevenue> monthlyRevenues = new ArrayList<>();
        for (Object[] row : monthlyData) {
            BigDecimal monthAmount = (BigDecimal) row[2];
            monthlyRevenues.add(AdminDashboardStats.MonthlyRevenue.builder()
                    .year((Integer) row[0])
                    .month((Integer) row[1])
                    .revenue(monthAmount)
                    .systemRevenue(monthAmount.multiply(COMMISSION_RATE).setScale(0, RoundingMode.HALF_UP))
                    .build());
        }

        // Counts
        long totalUsers = userRepository.count();
        long completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        long pendingDisputes = disputeRepository.countByStatus("PENDING");

        return ApiResponse.<AdminDashboardStats>builder()
                .result(AdminDashboardStats.builder()
                        .totalRevenue(totalRevenue)
                        .systemRevenue(systemRevenue)
                        .totalUsers(totalUsers)
                        .totalCompletedBookings(completedBookings)
                        .pendingWithdrawals(0)
                        .pendingDisputes(pendingDisputes)
                        .monthlyRevenues(monthlyRevenues)
                        .build())
                .build();
    }
}
