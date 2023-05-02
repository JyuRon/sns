package com.example.config;

import com.example.config.filter.JwtTokenFilter;
import com.example.exception.CustomAuthenticationEntryPoint;
import com.example.service.UserService;
import com.example.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserService userService,
            JwtTokenUtils jwtTokenUtils
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                                .mvcMatchers("/api/*/users/join","/api/*/users/login").permitAll()
                                .mvcMatchers("/api/**").authenticated()
                        )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf((csrf) -> csrf.disable())
                .addFilterBefore(new JwtTokenFilter(userService,jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                // Http403ForbiddenEntryPoint 커스텀 설정
                .exceptionHandling(exceptionConfig -> exceptionConfig.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
