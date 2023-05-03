package com.example.dto.response;

import com.example.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String body;
    private UserResponse user;
    protected LocalDateTime registerAt;
    protected LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static PostResponse fromDto(PostDto postDto){
        return PostResponse.of(
                postDto.getId(),
                postDto.getTitle(),
                postDto.getBody(),
                UserResponse.fromUserDto(postDto.getUser()),
                postDto.getRegisterAt(),
                postDto.getUpdatedAt(),
                postDto.getDeletedAt()
        );
    }

    public static PostResponse of(Long id, String title, String body, UserResponse user, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new PostResponse(id, title, body, user, registerAt, updatedAt, deletedAt);
    }
}
