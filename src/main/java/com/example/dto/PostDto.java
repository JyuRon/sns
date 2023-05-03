package com.example.dto;

import com.example.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String body;
    private UserDto user;
    protected LocalDateTime registerAt;
    protected LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static PostDto fromEntity(Post post){
        return PostDto.of(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserDto.fromEntity(post.getUser()),
                post.getRegisterAt(),
                post.getUpdatedAt(),
                post.getDeletedAt()
        );
    }

    public static PostDto of(Long id, String title, String body, UserDto user, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PostDto(id, title, body, user, registerAt, updatedAt, deletedAt);
    }
}
