package com.example.controller;

import com.example.config.SecurityConfig;
import com.example.config.TestSecurityConfig;
import com.example.dto.request.PostCreatRequest;
import com.example.service.PostService;
import com.example.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        willDoNothing().given(postService).create(anyString(),anyString(),anyString());

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/posts")
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
        willDoNothing().given(postService).create(anyString(),anyString(),anyString());

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



}