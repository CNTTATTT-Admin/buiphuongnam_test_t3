package vn.kurisu.mentormatch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Integer> {
    Page<Dispute> findByCreatorIdOrderByCreatedAtDesc(Integer creatorId, Pageable pageable);
    Page<Dispute> findByBooking_TimeSlot_MentorIdOrderByCreatedAtDesc(Integer mentorId, Pageable pageable);
    long countByStatus(String status);
    java.util.Optional<Dispute> findByBookingId(Integer bookingId);
}
