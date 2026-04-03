package vn.kurisu.mentormatch.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateResponseDto {
    private Integer id;
    private String name;
    private String fileUrl;
    private Boolean isApproved;
}
