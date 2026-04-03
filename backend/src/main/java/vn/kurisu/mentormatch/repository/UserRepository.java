package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.kurisu.mentormatch.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRolesName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.roles r " +
           "LEFT JOIN u.mentorProfile mp " +
           "LEFT JOIN mp.skills s " +
           "WHERE r.name = 'ROLE_MENTOR' AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(mp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(mp.bio) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchMentorsByKeyword(@Param("keyword") String keyword);
}
