package vn.kurisu.mentormatch.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Integer id;
    private Integer bookingId;
    private Integer menteeId;
    private String menteeName;
    private String menteeAvatar;
    private Integer mentorId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
