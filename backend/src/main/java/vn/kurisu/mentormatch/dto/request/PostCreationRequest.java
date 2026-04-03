package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreationRequest {

    @NotBlank(message = "Post content cannot be empty")
    private String content;

    private List<String> imageUrls;
}
