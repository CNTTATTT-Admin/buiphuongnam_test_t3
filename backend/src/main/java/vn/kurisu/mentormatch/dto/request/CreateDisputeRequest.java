package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDisputeRequest {
    private Integer bookingId;
    
    @NotBlank(message = "Lý do khiếu nại không được để trống")
    private String reason;
}
