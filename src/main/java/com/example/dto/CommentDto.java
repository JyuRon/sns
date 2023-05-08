package com.example.dto;

import com.example.domain.Comment;
import com.example.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    protected LocalDateTime registerAt;
    protected LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static CommentDto fromEntity(Comment comment){
        return CommentDto.of(
                comment.getId(),
                comment.getComment(),
                comment.getUserAccount().getUserName(),
                comment.getPost().getId(),
                comment.getRegisterAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }

    public static CommentDto of(Long id, String comment, String userName, Long postId, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new CommentDto(id, comment, userName, postId, registerAt, updatedAt, deletedAt);
    }
}
