package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByBookingId(Integer bookingId);
    List<Review> findByMentorIdOrderByCreatedAtDesc(Integer mentorId);
    boolean existsByBookingId(Integer bookingId);
}
