package vn.kurisu.mentormatch.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateDto {
    private String name;
    private String fileUrl;
}
