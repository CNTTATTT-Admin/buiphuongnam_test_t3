package vn.kurisu.mentormatch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    long countByUserIdAndIsReadFalse(Integer userId);
}
