package vn.kurisu.mentormatch.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    private String content;
    private List<String> imageUrls;
}
