package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.mentor.id = :mentorId ORDER BY b.timeSlot.startTime ASC")
    List<Booking> findBookingsByMentorId(@Param("mentorId") Integer mentorId);
    
    List<Booking> findByMenteeIdOrderByCreatedAtDesc(Integer menteeId);
}
