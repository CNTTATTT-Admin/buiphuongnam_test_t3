package vn.kurisu.mentormatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String title;
    private String message;
    private String type;
    private Integer referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
