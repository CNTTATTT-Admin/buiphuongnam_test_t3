package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotRequest {
    @NotNull(message = "START_TIME_REQUIRED")
    @Future(message = "START_TIME_MUST_BE_FUTURE")
    private LocalDateTime startTime;

    @NotNull(message = "END_TIME_REQUIRED")
    @Future(message = "END_TIME_MUST_BE_FUTURE")
    private LocalDateTime endTime;

    @NotNull(message = "PRICE_REQUIRED")
    @PositiveOrZero(message = "PRICE_INVALID")
    private BigDecimal price;
}
