package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.ChatMessageResponse;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.service.ChatMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/conversations/{targetUserId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getConversation(
            @PathVariable Integer targetUserId,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<ChatMessageResponse> messages = chatMessageService.getConversation(
                authentication.getName(),
                targetUserId
        );

        return ResponseEntity.ok(
                ApiResponse.<List<ChatMessageResponse>>builder()
                        .code(1000)
                        .message("Conversation fetched successfully")
                        .result(messages)
                        .build()
        );
    }
}
