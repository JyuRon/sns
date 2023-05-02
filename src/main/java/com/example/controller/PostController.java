package com.example.controller;

import com.example.dto.request.PostCreatRequest;
import com.example.dto.response.Response;
import com.example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(
            @RequestBody PostCreatRequest request,
            Authentication authentication
    ){
        postService.create(request.getTitle(), request.getBody(), authentication.getName());


        return Response.success();
    }
}
