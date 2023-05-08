package com.example.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    NEW_COMMENT_ON_POST("new comment!"),
    LIKE_ON_POST("new like!"),
    ;

    private final String alarmText;
}
