package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private String content;
    private LocalDateTime createdAt;
}
