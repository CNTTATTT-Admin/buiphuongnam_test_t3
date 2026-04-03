package vn.kurisu.mentormatch.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenteeProfileRequest {
    private String currentEducation;
    private String learningGoals;
    private String interests;
}
