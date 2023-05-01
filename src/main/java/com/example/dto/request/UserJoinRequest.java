package com.example.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserJoinRequest {

    private final String userName;
    private final String password;
}
