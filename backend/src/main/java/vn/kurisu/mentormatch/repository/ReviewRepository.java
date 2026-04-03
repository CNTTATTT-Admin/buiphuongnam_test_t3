package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByBookingId(Integer bookingId);
    List<Review> findByMentorIdOrderByCreatedAtDesc(Integer mentorId);
    boolean existsByBookingId(Integer bookingId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.mentor.id = :mentorId")
    Double getAverageRatingByMentorId(@Param("mentorId") Integer mentorId);
}
