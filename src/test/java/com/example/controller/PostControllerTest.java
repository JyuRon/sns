package com.example.controller;

import com.example.config.TestSecurityConfig;
import com.example.constant.ErrorCode;
import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.dto.request.PostCommentRequest;
import com.example.dto.request.PostCreatRequest;
import com.example.exception.SnsApplicationException;
import com.example.service.PostService;
import com.example.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({FormDataEncoder.class, TestSecurityConfig.class})
@WebMvcTest(PostController.class) // Controller Test 기법, 특정 클래스 지정 없을 경우 모든 controller 호출
class PostControllerTest {

    private final MockMvc mockMvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private PostService postService;


    PostControllerTest(
            @Autowired MockMvc mockMvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mockMvc = mockMvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("포스트 작성")
    @Test
    @WithMockUser
    void givenTitleAndBody_whenCreatePost_thenReturnSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        willDoNothing().given(postService).create(anyString(),anyString(),anyLong());

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("포스트 작성시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenTitleAndBodyWithoutLogin_whenCreatePost_thenReturnException() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        willDoNothing().given(postService).create(anyString(),anyString(),anyLong());

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("포스트 수성")
    @Test
    @WithMockUser
    void givenTitleAndBody_whenModifiedPost_thenReturnSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        given(postService.modify(anyString(),anyString(),anyLong(), anyLong()))
                .willReturn(createPostDto(1L, title, body, createUserDto(1L, "jyuka")))
        ;

        // When & Then
        mockMvc
                .perform(
                        put("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("포스트 수정시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenTitleAndBodyWithoutLogin_whenModifyPost_thenReturnException() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        given(postService.modify(anyString(),anyString(),anyLong(), anyLong()))
                .willThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", "jyuka")));

        // When & Then
        mockMvc
                .perform(
                        put("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("포스트 수정시 본인이 작성한 글이 아닌 경우")
    @Test
    @WithMockUser
    void givenTitleAndBodyWithAnotherUser_whenModifyPost_thenReturnException() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        given(postService.modify(anyString(),anyString(),anyLong(), anyLong()))
                .willThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", "jyuka", 1L)));

        // When & Then
        mockMvc
                .perform(
                        put("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("포스트 수정시 수정하려는 글이 없는 경우")
    @Test
    @WithMockUser
    void givenNotExistPost_whenModifyPost_thenReturnException() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).modify(anyString(), anyString(), anyLong(), anyLong());
        // When & Then
        mockMvc
                .perform(
                        put("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new PostCreatRequest(title, body)))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("포스트 삭제")
    @Test
    @WithMockUser
    void givenPostIdAndUserName_whenDeletePost_thenReturnSuccess() throws Exception {
        // Given
        willDoNothing().given(postService).delete(anyLong(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        delete("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("포스트 삭제시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenPostIdWithoutLogin_whenDeletePost_thenReturnException() throws Exception {
        // Given
//        doThrow(new SnsApplicationException(ErrorCode.INVALID_TOKEN))
//                .when(postService).delete(anyString(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        delete("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("포스트 삭제시 본인이 작성한 글이 아닌 경우")
    @Test
    @WithMockUser
    void givenPostIdWithAnotherUser_whenDeletePost_thenReturnException() throws Exception {
        // Given
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).delete(anyLong(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        delete("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("포스트 삭제시 수정하려는 글이 없는 경우")
    @Test
    @WithMockUser
    void givenNotExistPost_whenDeletePost_thenReturnException() throws Exception {
        // Given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).delete(anyLong(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        delete("/api/v1/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    @DisplayName("피드 목록")
    @Test
    @WithMockUser
    void giveNothingWithLogin_whenSelectPostList_thenReturnSuccess() throws Exception {
        // Given
        given(postService.list(any(Pageable.class)))
                .willReturn(Page.empty());

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("피드목록 요청시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenNothing_whenSelectPostList_thenReturnFail() throws Exception {
        // Given

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("내 피드 목록")
    @Test
    @WithMockUser
    void giveNothingWithLogin_whenMySelectPostList_thenReturnSuccess() throws Exception {
        // Given
        given(postService.my(anyLong(), any(Pageable.class)))
                .willReturn(Page.empty());

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts/my")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("내 피드목록 요청시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenNothing_whenSelectMyPostList_thenReturnFail() throws Exception {
        // Given

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts/my")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("좋아요 적용/취소 성공")
    @Test
    @WithMockUser
    void givenNothing_whenClickLikeButton_thenReturnSuccess() throws Exception {
        // Given
        willDoNothing().given(postService).like(anyLong(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/likes")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        then(postService).should().like(anyLong(), anyLong());
    }

    @DisplayName("좋아요 기능 클릭시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenWithoutLogin_whenClickLikeButton_thenReturnFail() throws Exception {
        // Given

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/likes")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("좋아요 기능 클릭시 게시물이 없는 경우")
    @Test
    @WithMockUser
    void givenNotExistPost_whenClickLikeButton_thenReturnFail() throws Exception {
        // Given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).like(anyLong(), anyLong());

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/likes")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("댓글 등록 성공")
    @Test
    @WithMockUser
    void givenCommentRequestAndPostIdAndUserName_whenAddComment_thenReturnSuccess() throws Exception {
        // Given
        String comment = "is comment";
        PostCommentRequest postCommentRequest = PostCommentRequest.of(comment);
        willDoNothing().given(postService).comment(anyLong(), anyLong(), any(PostCommentRequest.class));

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(postCommentRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        then(postService).should().comment(anyLong(), anyLong(), any(PostCommentRequest.class));
    }

    @DisplayName("댓글 작성시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenCommentRequestAndPostId_whenAddComment_thenReturnUnAuthorizedException() throws Exception {
        // Given
        String comment = "is comment";
        PostCommentRequest postCommentRequest = PostCommentRequest.of(comment);

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(postCommentRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("댓글 작성시 게시물이 없는 경우")
    @Test
    @WithMockUser
    void givenCommentRequestAndPostIdAndUserName_whenAddComment_thenReturnPostNotFoundException() throws Exception {
        // Given
        String comment = "is comment";
        PostCommentRequest postCommentRequest = PostCommentRequest.of(comment);
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).comment(anyLong(), anyLong(), any(PostCommentRequest.class));

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts/1/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(postCommentRequest))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
        then(postService).should().comment(anyLong(), anyLong(), any(PostCommentRequest.class));
    }


    @DisplayName("댓글 목록 조회 성공")
    @Test
    @WithMockUser
    void givenPostIdAndPageable_whenSelectCommentList_thenReturnSuccess() throws Exception {
        // Given
        Long postId = 1L;
        Pageable pageable = Pageable.ofSize(20);
        given(postService.getComments(postId, pageable))
                .willReturn(Page.empty());

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts/" + postId + "/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        then(postService).should().getComments(postId,pageable);
    }

    @DisplayName("댓글 작성시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenPostIdAndPageable_whenSelectCommentList_thenUnAuthorizedException() throws Exception {
        // Given

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }


    private static PostDto createPostDto(Long id, String title, String body, UserDto userDto){
        return PostDto.of(id, title, body, userDto, null, null, null);
    }

    private static UserDto createUserDto(Long id, String userName){
        return UserDto.of(id, userName, null, null, null,null,null);
    }
}