package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterDisputeRequest {
    @NotBlank(message = "Lý do kháng cáo không được để trống")
    private String counterReason;
}
