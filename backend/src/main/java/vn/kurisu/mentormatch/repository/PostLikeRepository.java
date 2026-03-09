package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.PostLike;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostIdAndUserId(Integer postId, Integer userId);
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    int countByPostId(Integer postId);
}
