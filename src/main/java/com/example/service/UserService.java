package com.example.service;

import com.example.constant.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.dto.UserDto;
import com.example.domain.UserAccount;
import com.example.repository.UserAccountRepository;
import com.example.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    @Transactional
    public UserDto join(String userName, String password){
        userEntityRepository.findByUserName(userName)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                })
        ;

        UserAccount userAccount = userEntityRepository.save(UserAccount.of(userName, passwordEncoder.encode(password)));

        return UserDto.fromEntity(userAccount);
    }

    public String login(String userName, String password){
        // 회원 여부 확인
        UserAccount userAccount = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));

        // 패스워트 확인
        if(!passwordEncoder.matches(password, userAccount.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);


        return token;
    }

    public UserDto loadUserByUserName(String userName){
        return userEntityRepository.findByUserName(userName)
                .map(UserDto::fromEntity)
                .orElseThrow(() ->
                    new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded",userName)));
    }

     void settingJwtValues(String key, Long expiredTimeMs){
        this.secretKey = key;
        this.expiredTimeMs = expiredTimeMs;
    }
}
