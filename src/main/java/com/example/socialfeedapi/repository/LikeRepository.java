package com.example.socialfeedapi.repository;

import com.example.socialfeedapi.entity.Like;
import com.example.socialfeedapi.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    List<Like> findByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);


}
