package com.example.service;

import com.example.constant.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.fixture.UserAccountFixture;
import com.example.domain.UserAccount;
import com.example.repository.AlarmRepository;
import com.example.repository.UserAccountRepository;
import com.example.repository.UserCacheRepository;
import com.example.util.JwtTokenUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserAccountRepository userEntityRepository;
    @Mock private AlarmRepository alarmRepository;
    @Mock private BCryptPasswordEncoder encoder;
    @Mock private JwtTokenUtils jwtTokenUtils;
    @Mock private UserCacheRepository userCacheRepository;

    @DisplayName("회원가입이 정상적으로 동작하는 경우")
    @Test
    void givenUserNameAndPassword_whenSignUp_thenReturnSuccess(){
        // Given
        String userName = "userName";
        String password = "password";
        UserAccount userEntity = UserAccountFixture.get(userName, password);

        given(userEntityRepository.findByUserName(anyString()))
                .willReturn(Optional.empty());
        given(userEntityRepository.save(any(UserAccount.class)))
                .willReturn(userEntity);
        given(encoder.encode(password))
                .willReturn("encrypt_password");

        // When
        Throwable t = catchThrowable(() -> userService.join(userName, password));

        // Then
        assertThat(t).doesNotThrowAnyException();
    }


    @DisplayName("회원가입시 userName으로 회원가입한 유저가 이미 있는 경우")
    @Test
    void givenDuplicateUserName_whenSignUp_thenReturnException(){
        // Given
        String userName = "userName";
        String password = "password";
        UserAccount userAccount = UserAccountFixture.get(userName, password);

        given(userEntityRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));

        // When
        Throwable t = catchThrowable(() -> userService.join(userName, password));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.DUPLICATED_USER_NAME.getMessage(), String.format("%s is duplicated",userName)))
        ;
    }

    @DisplayName("로그인이 정상적으로 동작하는 경우")
    @Test
    void givenUserNameAndPassword_whenLogin_thenReturnSuccess(){
        // Given
        String userName = "userName";
        String password = "password";

        UserAccount userEntity = UserAccountFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName))
                .willReturn(Optional.of(userEntity));
        given(encoder.matches(password, userEntity.getPassword()))
                .willReturn(true);
        given(jwtTokenUtils.generateToken(any())).willReturn("test_token");

        // When
        Throwable t = catchThrowable(() -> userService.login(userName, password));

        // Then
        assertThat(t).doesNotThrowAnyException();
    }


    @DisplayName("로그인시 userName으로 회원가입한 유저가 없는 경우")
    @Test
    void givenNotExistUserName_whenLogin_thenReturnException(){
        // Given
        String userName = "userName";
        String password = "password";

        given(userEntityRepository.findByUserName(userName))
                .willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> userService.login(userName, password));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.USER_NOT_FOUND.getMessage(), String.format("%s not founded",userName)))
        ;
    }

    @DisplayName("로그인시 패스워드가 틀린 경우")
    @Test
    void givenWrongPassword_whenLogin_thenReturnException(){
        // Given
        String userName = "userName";
        String password = "password";
        String inputPassword = "wrongPassword";
        UserAccount userEntity = UserAccountFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName))
                .willReturn(Optional.of(userEntity));

        // When
        Throwable t = catchThrowable(() -> userService.login(userName, inputPassword));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s", ErrorCode.INVALID_PASSWORD.getMessage()))
        ;
    }

    @DisplayName("알림 리스트 조회 성공")
    @Test
    void givenUserNameAndPageable_whenSelectAlarmList_thenReturnSuccess(){
        // Given
        Long userId = 1L;
        Pageable pageable = Pageable.ofSize(20);

        given(alarmRepository.findAllByUser_Id(userId, pageable))
                .willReturn(Page.empty());

        // When
        Throwable t = catchThrowable(() -> userService.alarmList(userId, pageable));

        // Then
        assertThat(t).doesNotThrowAnyException();
    }

    @Disabled("jwt 에서 이미 로그인 여부를 판단하기 때문에 비활성화")
    @DisplayName("알림 리스트 조회 시 로그인을 하지 않은 경우")
    @Test
    void givenPageable_whenSelectAlarmList_thenReturnUserNotFoundException(){
        // Given
        Long userId = 1L;
        Pageable pageable = Pageable.ofSize(20);

        // When
        Throwable t = catchThrowable(() -> userService.alarmList(userId, pageable));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.USER_NOT_FOUND.getMessage(), String.format("%s not founded",userId)))
        ;
    }

}