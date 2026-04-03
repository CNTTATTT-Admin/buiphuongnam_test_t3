package vn.kurisu.mentormatch.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminDashboardStats {
    private BigDecimal totalRevenue;          // sum(amount) of SUCCESS payments
    private BigDecimal systemRevenue;         // totalRevenue * 5%
    private long totalUsers;
    private long totalCompletedBookings;
    private long pendingWithdrawals;
    private long pendingDisputes;
    private List<MonthlyRevenue> monthlyRevenues;

    @Data
    @Builder
    public static class MonthlyRevenue {
        private int year;
        private int month;
        private BigDecimal revenue;       // sum(amount) for that month
        private BigDecimal systemRevenue; // revenue * 5%
    }
}
