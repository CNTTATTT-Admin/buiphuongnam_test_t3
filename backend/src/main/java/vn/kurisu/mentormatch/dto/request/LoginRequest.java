package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "USERNAME_INVALID")
    private String userName;

    @NotBlank(message = "PASSWORD_INVALID")
    private String password;
}
