package com.example.socialfeedapi.repository;

import com.example.socialfeedapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findTop20ByOrderByCreatedAtDesc();
    List<Post> findTop10ByCreatedAtAfterOrderByLikesCountDesc(LocalDateTime since);

}
