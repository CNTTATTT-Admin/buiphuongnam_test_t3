package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingActionRequest {
    @NotBlank(message = "ACTION_REQUIRED")
    private String action; // "CONFIRM" or "REJECT"
}
