package com.example.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRequest {
    private String comment;

    public static PostCommentRequest of(String comment){
        return new PostCommentRequest(comment);
    }
}
