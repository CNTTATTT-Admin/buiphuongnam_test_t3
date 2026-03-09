package vn.kurisu.mentormatch.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Integer id;
    private Integer userId;
    private String authorName;
    private String authorAvatarUrl;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
