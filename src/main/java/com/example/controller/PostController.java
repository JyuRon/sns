package com.example.controller;

import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.dto.request.PostCommentRequest;
import com.example.dto.request.PostCreatRequest;
import com.example.dto.request.PostModifyRequest;
import com.example.dto.response.CommentResponse;
import com.example.dto.response.PostResponse;
import com.example.dto.response.Response;
import com.example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(
            @RequestBody PostCreatRequest request,
            @AuthenticationPrincipal UserDto userDto
    ){
        postService.create(request.getTitle(), request.getBody(), userDto.getUsername());


        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(
            @RequestBody PostModifyRequest request,
            @AuthenticationPrincipal UserDto userDto,
            @PathVariable Long postId
    ){
        PostDto postDto = postService.modify(request.getTitle(), request.getBody(), userDto.getUsername(), postId);

        return Response.success(PostResponse.fromDto(postDto));
    }

    @DeleteMapping("{postId}")
    public Response<Void> delete(
            @AuthenticationPrincipal UserDto userDto,
            @PathVariable Long postId
    ){
        postService.delete(userDto.getUsername(), postId);
        return Response.success();
    }


    @GetMapping
    public Response<Page<PostResponse>> list(
            Pageable pageable,
            @AuthenticationPrincipal UserDto userDto
    ){
        Page<PostResponse> result = postService.list(pageable).map(PostResponse::fromDto);
        return Response.success(result);
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(
            Pageable pageable,
            @AuthenticationPrincipal UserDto userDto
    ){
        Page<PostResponse> result = postService.my(userDto.getUsername(), pageable).map(PostResponse::fromDto);
        return Response.success(result);
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDto userDto
    ){
        postService.like(postId, userDto.getUsername());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDto userDto
    ){
        return Response.success(postService.likeCount(postId));
    }


    @PostMapping("/{postId}/comments")
    public Response<Void> comment(
            @PathVariable Long postId,
            @RequestBody PostCommentRequest request,
            @AuthenticationPrincipal UserDto userDto
    ){
        postService.comment(postId, userDto.getUsername(), request);
        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comment(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDto userDto,
            Pageable pageable
    ){
        return Response.success(
                postService.getComments(postId, pageable)
                .map(CommentResponse::fromDto)
        );
    }

}
