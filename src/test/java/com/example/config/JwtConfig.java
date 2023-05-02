package com.example.config;

import com.example.util.JwtTokenUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class JwtConfig {

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @BeforeTestMethod
    public void jwtSetup(){
        jwtTokenUtils.setKey("secret-key");
        jwtTokenUtils.setExpiredTimeMs(10000L);
        given(jwtTokenUtils.isExpired(anyString())).willReturn(false);
        given(jwtTokenUtils.getUserName(anyString())).willReturn("testuser");
    }
}
