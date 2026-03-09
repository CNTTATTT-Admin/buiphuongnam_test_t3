package vn.kurisu.mentormatch.dto.response;

import lombok.*;
import vn.kurisu.mentormatch.entity.SlotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotResponse {
    private Integer id;
    private Integer mentorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private SlotStatus status;
}
