package com.example.controller;

import com.example.dto.request.UserJoinRequest;
import com.example.dto.response.Response;
import com.example.dto.response.UserJoinResponse;
import com.example.dto.UserDto;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request){
        UserDto user = userService.join(request.getUserName(), request.getPassword());

        return Response.success(UserJoinResponse.fromUser(user));
    }
}