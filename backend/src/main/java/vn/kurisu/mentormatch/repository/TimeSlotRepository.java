package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByMentorIdOrderByStartTimeAsc(Integer mentorId);
    
    @Query("SELECT t FROM TimeSlot t WHERE t.mentor.id = :mentorId AND t.startTime >= :now ORDER BY t.startTime ASC")
    List<TimeSlot> findUpcomingSlotsByMentorId(@Param("mentorId") Integer mentorId, @Param("now") LocalDateTime now);
}
