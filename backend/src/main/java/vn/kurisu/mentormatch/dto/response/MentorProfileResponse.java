package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorProfileResponse {
    private Integer id;
    private String title;
    private String bio;
    private Integer yearsOfExperience;
    private Boolean isVerified;
    private BigDecimal walletBalance;
    private List<String> skills;
    private List<CertificateResponseDto> certificates;
    private Double rating;
    private Long totalStudents;
    private Integer reviewCount;
}
