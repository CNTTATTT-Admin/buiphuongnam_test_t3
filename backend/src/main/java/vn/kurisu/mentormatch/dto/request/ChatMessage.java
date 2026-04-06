package vn.kurisu.mentormatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @NotNull(message = "USER_NOT_EXISTED")
    private Integer receiverId;

    @NotBlank(message = "CHAT_MESSAGE_EMPTY")
    private String content;
}
