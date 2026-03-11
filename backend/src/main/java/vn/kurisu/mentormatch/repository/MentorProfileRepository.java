package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.MentorProfile;

import java.util.List;
import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Integer> {
    Optional<MentorProfile> findByUserId(Integer userId);
    
    List<MentorProfile> findByIsVerifiedFalse();
}
