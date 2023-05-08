package com.example.dto.response;

import com.example.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private LocalDateTime registerAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static CommentResponse fromDto(CommentDto commentDto){
        return CommentResponse.of(
                commentDto.getId(),
                commentDto.getComment(),
                commentDto.getUserName(),
                commentDto.getPostId(),
                commentDto.getRegisterAt(),
                commentDto.getUpdatedAt(),
                commentDto.getDeletedAt()
        );
    }

    public static CommentResponse of(Long id, String comment, String userName, Long postId, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt){
        return new CommentResponse(id, comment, userName, postId, registerAt, updatedAt, deletedAt);
    }
}
