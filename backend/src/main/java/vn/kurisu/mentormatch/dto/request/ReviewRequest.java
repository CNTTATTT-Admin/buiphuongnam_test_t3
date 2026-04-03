package vn.kurisu.mentormatch.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {
    private Integer bookingId;
    private Integer rating;
    private String comment;
}
