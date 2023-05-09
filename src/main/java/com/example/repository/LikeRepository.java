package com.example.repository;

import com.example.domain.Like;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(Long postId);

    @Transactional
    @Modifying
    @Query("update Like l set l.deletedAt = now() where l.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
