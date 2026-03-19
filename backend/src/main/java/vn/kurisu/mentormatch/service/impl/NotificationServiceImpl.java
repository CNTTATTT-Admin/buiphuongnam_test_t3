package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.NotificationResponse;
import vn.kurisu.mentormatch.entity.Notification;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.NotificationRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.NotificationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @Override
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<NotificationResponse> responses = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(this::mapToResponse);

        return ApiResponse.<Page<NotificationResponse>>builder()
                .result(responses)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAsRead(Integer id) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
             throw new RuntimeException("Not authorized");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);

        return ApiResponse.<Void>builder()
                .message("Marked as read")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), Pageable.unpaged())
                .stream()
                .filter(n -> !n.getIsRead())
                .toList();

        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);

        return ApiResponse.<Void>builder()
                .message("All marked as read")
                .build();
    }

    @Override
    public ApiResponse<Long> getUnreadCount() {
        User currentUser = getCurrentUser();
        long count = notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
        return ApiResponse.<Long>builder()
                .result(count)
                .build();
    }

    @Override
    @Transactional
    public void sendNotification(User user, String title, String message, String type, Integer referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
