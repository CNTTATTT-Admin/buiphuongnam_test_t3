package vn.kurisu.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.kurisu.mentormatch.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
