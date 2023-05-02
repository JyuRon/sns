package com.example.config;

import com.example.constant.UserRole;
import com.example.dto.UserDto;
import com.example.service.UserService;
import com.example.util.JwtTokenUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    // Spring에서 제공하는 테스트 메서드로 스프링 관련(인증) 테스트를 진행할때만 사용 가능
    @BeforeTestMethod
    public void SecuritySetup(){
        given(userService.loadUserByUserName(anyString()))
                .willReturn(createUserAccountDto());
        jwtTokenUtils.setKey("secret-key");
        jwtTokenUtils.setExpiredTimeMs(10000L);
        given(jwtTokenUtils.isExpired(anyString())).willReturn(false);
        given(jwtTokenUtils.getUserName(anyString())).willReturn("testuser");
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
