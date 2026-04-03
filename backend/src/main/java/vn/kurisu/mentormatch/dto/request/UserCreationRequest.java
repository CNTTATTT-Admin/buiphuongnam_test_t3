package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {

    @NotBlank(message = "USERNAME_INVALID")
    private String userName;

    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;
    
    private Boolean isActive;
    
    private Set<String> roles;
}
