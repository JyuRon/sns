package com.example.service;

import com.example.constant.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.fixture.UserAccountFixture;
import com.example.domain.UserAccount;
import com.example.repository.UserAccountRepository;
import com.example.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserAccountRepository userEntityRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void beforeAll(){
        userService.settingJwtValues("Task :prepareKotlinBuildScriptModel UP-TO-DATE",100000L);
    }

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
}