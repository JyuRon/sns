package com.example.repository;

import com.example.domain.Like;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(UserAccount userAccount, Post post);
    Long countByPost(Post post);
}
