package com.example.dto.response;

import com.example.dto.UserDto;
import com.example.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinResponse {

    private Long id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(UserDto user){
        return new UserJoinResponse(
                user.getId(),
                user.getUserName(),
                user.getUserRole()
        );
    }
}
