package vn.kurisu.mentormatch.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventDto {
    private Long userId;        // Người nhận (Mentor hoặc Mentee)
    private String title;       // Tiêu đề
    private String message;     // Nội dung
    private String type;        // BOOKING, DISPUTE, SYSTEM...
    private Long referenceId;   // ID của ca học/khiếu nại
}
