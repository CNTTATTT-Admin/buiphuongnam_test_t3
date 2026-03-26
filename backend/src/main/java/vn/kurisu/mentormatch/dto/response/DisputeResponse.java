package vn.kurisu.mentormatch.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DisputeResponse {
    private Integer id;
    private Integer bookingId;
    private Integer creatorId;
    private String creatorName;
    private String reason;
    private String status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
