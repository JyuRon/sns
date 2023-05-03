package com.example.dto.response;

import com.example.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String userName;

    public static UserResponse fromUserDto(UserDto user) {
        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }

}
