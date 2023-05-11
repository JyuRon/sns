package com.example.controller;

import com.example.config.JwtConfig;
import com.example.constant.ErrorCode;
import com.example.constant.UserRole;
import com.example.dto.UserDto;
import com.example.dto.request.UserJoinRequest;
import com.example.dto.request.UserLoginRequest;
import com.example.exception.SnsApplicationException;
import com.example.fixture.UserAccountFixture;
import com.example.service.AlarmService;
import com.example.service.UserService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({FormDataEncoder.class, JwtConfig.class})
@WebMvcTest(UserController.class) // Controller Test 기법, 특정 클래스 지정 없을 경우 모든 controller 호출
class UserControllerTest {

    private final MockMvc mockMvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private UserService userService;
    @MockBean private AlarmService alarmService;

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

    @DisplayName("알림 리스트를 정상적으로 호출한다.")
    @Test
    @WithMockUser
    void givenUserNameAndPageable_whenSelectAlarmList_thenReturnSuccess() throws Exception{
        //Given
        given(userService.loadUserByUserName(anyString()))
                .willReturn(createUserAccountDto());
        given(userService.alarmList(anyLong(), any(Pageable.class)))
                .willReturn(Page.empty());

        //When & Then
        mockMvc
                .perform(
                        get("/api/v1/users/alarm")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("알림 리스트 호출 시 로그인을 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenUserNameAndPageable_whenSelectAlarmList_thenUnAuthorizedException() throws Exception{
        //Given

        //When & Then
        mockMvc
                .perform(
                        get("/api/v1/users/alarm")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("알림 SSE 접속시도후 성공한다.")
    @Test
    @WithMockUser
    void givenQueryParamWithJwtToken_whenConectSSE_thenReturnSuccess() throws Exception{
        //Given
        given(userService.loadUserByUserName(anyString()))
                .willReturn(createUserAccountDto());
        given(alarmService.connectAlarm(anyLong())).willReturn(any(SseEmitter.class));
        //When & Then
        mockMvc
                .perform(
                        get("/api/v1/users/alarm/subscribe")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("token","Bearer testToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("알림 SSE 접속시도 실패시 에러를 반환한다.")
    @Test
    @WithMockUser
    void givenQueryParamWithJwtToken_whenDisConectSSE_thenReturnException() throws Exception{
        //Given
        given(userService.loadUserByUserName(anyString()))
                .willReturn(createUserAccountDto());
        given(alarmService.connectAlarm(anyLong()))
                .willThrow(new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR));
        //When & Then
        mockMvc
                .perform(
                        get("/api/v1/users/alarm/subscribe")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("token","Bearer testToken")
                )
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    private UserDto createUserAccountDto(){
        return UserDto.of(
                1L,
                "jyukaTest",
                "pw",
                UserRole.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}