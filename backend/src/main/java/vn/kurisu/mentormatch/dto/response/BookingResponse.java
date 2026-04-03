package vn.kurisu.mentormatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.kurisu.mentormatch.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Integer id;
    private Integer menteeId;
    private String menteeName;
    private String menteeAvatar;
    private Integer mentorId;
    private String mentorName;
    private String mentorAvatar;
    private Integer timeSlotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String menteeNotes;
    private String meetingLink;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private BigDecimal price;
}
