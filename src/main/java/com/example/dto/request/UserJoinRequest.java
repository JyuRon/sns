package com.example.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserJoinRequest {

    private final String name;
    private final String password;
}
