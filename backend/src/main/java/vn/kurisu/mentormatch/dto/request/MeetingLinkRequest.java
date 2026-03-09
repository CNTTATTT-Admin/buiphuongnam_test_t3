package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingLinkRequest {
    @NotBlank(message = "MEETING_LINK_REQUIRED")
    private String meetingLink;
}
