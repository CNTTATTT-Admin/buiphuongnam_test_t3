package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull(message = "Time slot ID is required")
    private Integer timeSlotId;
    
    private String menteeNotes;
}
