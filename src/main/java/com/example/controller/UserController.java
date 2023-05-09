package com.example.controller;

import com.example.dto.request.UserJoinRequest;
import com.example.dto.request.UserLoginRequest;
import com.example.dto.response.AlarmResponse;
import com.example.dto.response.Response;
import com.example.dto.response.UserJoinResponse;
import com.example.dto.UserDto;
import com.example.dto.response.UserLoginResponse;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request){
        UserDto user = userService.join(request.getName(), request.getPassword());

        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(
            @AuthenticationPrincipal UserDto userDto,
            Pageable pageable
    ){
        return Response.success(
                userService.alarmList(userDto.getId(), pageable)
                .map(AlarmResponse::fromDto)
        );
    }
}
