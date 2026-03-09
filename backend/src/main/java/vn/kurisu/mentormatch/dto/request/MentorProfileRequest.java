package vn.kurisu.mentormatch.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorProfileRequest {
    private String title;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> skills;
    private List<CertificateDto> certificates;
}
