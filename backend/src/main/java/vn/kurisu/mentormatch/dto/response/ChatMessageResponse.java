package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private Integer senderId;
    private String senderUserName;
    private String senderFullName;
    private String senderAvatarUrl;
    private Integer receiverId;
    private String content;
    private LocalDateTime createdAt;
}
