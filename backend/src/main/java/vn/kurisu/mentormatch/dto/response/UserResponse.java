package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String userName;
    private String fullName;
    private String avatarUrl;
    private Boolean isActive;
    private Set<RoleResponse> roles;
}
