package vn.kurisu.mentormatch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.ChatMessage;
import vn.kurisu.mentormatch.dto.response.ChatMessageResponse;
import vn.kurisu.mentormatch.entity.ChatMessageEntity;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.ChatMessageRepository;
import vn.kurisu.mentormatch.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(String senderUserName, ChatMessage request) {
        String content = request.getContent() != null ? request.getContent().trim() : "";
        if (content.isBlank()) {
            throw new AppException(ErrorCode.CHAT_MESSAGE_EMPTY);
        }

        if (request.getReceiverId() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User sender = userRepository.findByUserName(senderUserName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        ChatMessageEntity savedMessage = chatMessageRepository.save(
                ChatMessageEntity.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .content(content)
                        .build()
        );

        return toResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(String currentUserName, Integer targetUserId) {
        User currentUser = userRepository.findByUserName(currentUserName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!userRepository.existsById(targetUserId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        return chatMessageRepository.findConversation(currentUser.getId(), targetUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ChatMessageResponse toResponse(ChatMessageEntity message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderUserName(message.getSender().getUserName())
                .senderFullName(message.getSender().getFullName())
                .senderAvatarUrl(message.getSender().getAvatarUrl())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
