package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.MenteeProfile;

import java.util.Optional;

public interface MenteeProfileRepository extends JpaRepository<MenteeProfile, Integer> {
    Optional<MenteeProfile> findByUserId(Integer userId);
}
