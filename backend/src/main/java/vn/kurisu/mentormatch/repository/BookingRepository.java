package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.mentor.id = :mentorId AND (:status IS NULL OR b.status = :status) ORDER BY b.timeSlot.startTime ASC")
    org.springframework.data.domain.Page<Booking> findBookingsByMentorId(@Param("mentorId") Integer mentorId, @Param("status") vn.kurisu.mentormatch.entity.BookingStatus status, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.mentee.id = :menteeId AND (:status IS NULL OR b.status = :status) ORDER BY b.createdAt DESC")
    org.springframework.data.domain.Page<Booking> findByMenteeIdOrderByCreatedAtDesc(@Param("menteeId") Integer menteeId, @Param("status") vn.kurisu.mentormatch.entity.BookingStatus status, org.springframework.data.domain.Pageable pageable);
}
