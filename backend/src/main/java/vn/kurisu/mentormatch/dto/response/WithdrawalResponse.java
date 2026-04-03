package vn.kurisu.mentormatch.dto.response;

import lombok.*;
import vn.kurisu.mentormatch.entity.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalResponse {
    private Integer id;
    private Integer mentorProfileId;
    private String mentorName;
    private BigDecimal amount;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String note;
    private String adminNote;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
