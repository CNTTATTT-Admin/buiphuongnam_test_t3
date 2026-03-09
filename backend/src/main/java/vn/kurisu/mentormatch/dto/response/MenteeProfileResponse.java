package vn.kurisu.mentormatch.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenteeProfileResponse {
    private Integer id;
    private String currentEducation;
    private String learningGoals;
    private String interests;
}
