package vn.kurisu.mentormatch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.kurisu.mentormatch.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Integer postId);
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Integer postId, Pageable pageable);
}
