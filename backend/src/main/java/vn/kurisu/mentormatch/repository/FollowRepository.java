package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kurisu.mentormatch.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    
    // Check if a user is following another user
    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    // Get the follow record
    Optional<Follow> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    // Get list of users the given user is following
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower.id = :followerId ORDER BY f.createdAt DESC")
    List<Follow> findByFollowerIdWithFollowing(@Param("followerId") Integer followerId);

    // Get list of followers for the given user
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following.id = :followingId ORDER BY f.createdAt DESC")
    List<Follow> findByFollowingIdWithFollower(@Param("followingId") Integer followingId);
    
    // Count followers
    long countByFollowingId(Integer followingId);
    
    // Count following
    long countByFollowerId(Integer followerId);
}
