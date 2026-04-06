package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.kurisu.mentormatch.entity.ChatMessageEntity;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("""
            SELECT m FROM ChatMessageEntity m
            WHERE (m.sender.id = :userId AND m.receiver.id = :targetId)
               OR (m.sender.id = :targetId AND m.receiver.id = :userId)
            ORDER BY m.createdAt ASC, m.id ASC
            """)
    List<ChatMessageEntity> findConversation(
            @Param("userId") Integer userId,
            @Param("targetId") Integer targetId
    );
}
