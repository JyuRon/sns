package com.example.controller;

import com.example.config.SecurityConfig;
import com.example.constant.ErrorCode;
import com.example.dto.request.UserJoinRequest;
import com.example.dto.request.UserLoginRequest;
import com.example.exception.SnsApplicationException;
import com.example.dto.UserDto;
import com.example.fixture.UserAccountFixture;
import com.example.service.UserService;
import com.example.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({FormDataEncoder.class, SecurityConfig.class})
@WebMvcTest(UserController.class) // Controller Test 기법, 특정 클래스 지정 없을 경우 모든 controller 호출
class UserControllerTest {

    private final MockMvc mockMvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private UserService userService;

    UserControllerTest(
            @Autowired MockMvc mockMvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mockMvc = mockMvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("회원가입")
    @Test
    void givenUserNameAndPassword_whenSignUp_thenReturnSuccess() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";
        given(userService.join(anyString(), anyString()))
                .willReturn(UserDto.fromEntity(UserAccountFixture.get(userName, password)));

        // When & Then
        mockMvc
                .perform(
                    post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formDataEncoder.objectToJson(new UserJoinRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }


    @DisplayName("회원가입시 이미 회원가입한 userName으로 회원가입하는 경우")
    @Test
    void givenDuplicateUserName_whenSignUp_thenReturnFail() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";
        given(userService.join(userName, password))
                .willThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName)));

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/users/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new UserJoinRequest(userName,password)))
                )
                .andDo(print())
                .andExpect(status().isConflict())
        ;
    }


    @DisplayName("로그인 성공")
    @Test
    void givenUserNameAndPassword_whenLogin_thenReturnSuccess() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";
        given(userService.login(userName, password)).willReturn("test_token");

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new UserLoginRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }


    @DisplayName("로그인시 회원가입이 안된 userName 을 입력할 경우 에러 반환")
    @Test
    void givenNotSignUpUserName_whenLogin_thenReturnException() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";
        given(userService.login(userName, password))
                .willThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND,""));

        // When & Then
        mockMvc
                .perform(
                        post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new UserLoginRequest(userName,password)))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("로그인시 틀린 password를 을 입력할 경우 에러 반환")
    @Test
    void givenWrongPassword_whenLogin_thenReturnException() throws Exception {
        //Given
        String userName = "userName";
        String password = "password";

        given(userService.login(userName, password))
                .willThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD,""));

        //When & Then
        mockMvc
                .perform(
                        post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(formDataEncoder.objectToJson(new UserLoginRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }
}