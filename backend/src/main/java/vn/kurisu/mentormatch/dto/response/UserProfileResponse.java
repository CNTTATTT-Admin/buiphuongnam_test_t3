package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Integer id;
    private String userName;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phone;
    private List<String> roles;
    private MentorProfileResponse mentorProfile;
    private MenteeProfileResponse menteeProfile;
}
