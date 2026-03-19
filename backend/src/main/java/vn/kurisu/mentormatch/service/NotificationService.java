package vn.kurisu.mentormatch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.NotificationResponse;
import vn.kurisu.mentormatch.entity.User;

public interface NotificationService {
    ApiResponse<Page<NotificationResponse>> getMyNotifications(Pageable pageable);
    ApiResponse<Void> markAsRead(Integer id);
    ApiResponse<Void> markAllAsRead();
    ApiResponse<Long> getUnreadCount();
    void sendNotification(User user, String title, String message, String type, Integer referenceId);
}
