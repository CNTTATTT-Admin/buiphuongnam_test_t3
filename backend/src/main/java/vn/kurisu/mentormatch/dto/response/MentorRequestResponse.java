package vn.kurisu.mentormatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRequestResponse {
    private Integer profileId;
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    
    private String bio;
    private Integer yearsOfExperience;
    
    private List<String> skills;
    private List<CertificateDto> certificates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertificateDto {
        private Integer id;
        private String name;
        private String fileUrl;
    }
}
