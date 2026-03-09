package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Skill;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    Optional<Skill> findByName(String name);
}
