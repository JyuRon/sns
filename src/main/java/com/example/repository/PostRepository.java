package com.example.repository;

import com.example.domain.Post;
import com.example.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAllByUserId(Long userId, Pageable pageable);
}
