package com.example.repository;

import com.example.domain.Comment;
import com.example.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPost(Post post, Pageable pageable);
}
