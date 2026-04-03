package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    
    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    private String fullName;
    
    private String avatarUrl;

    private Boolean isActive;

    private Set<String> roles;
}
