package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileRequest {

    @NotBlank(message = "FULL_NAME_REQUIRED")
    private String fullName;

    private String avatarUrl;

    private String phone;
}
