package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveDisputeRequest {
    @NotBlank(message = "Lý do / nhận xét của Admin không được để trống")
    private String adminNote;
    
    // true: Hoàn tiền, false: Không hoàn tiền
    private Boolean acceptRefund; 
}
