package com.example.service;

import com.example.constant.ErrorCode;
import com.example.dto.AlarmDto;
import com.example.exception.SnsApplicationException;
import com.example.dto.UserDto;
import com.example.domain.UserAccount;
import com.example.repository.AlarmRepository;
import com.example.repository.UserAccountRepository;
import com.example.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userEntityRepository;
    private final AlarmRepository alarmRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;


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
        UserAccount userAccount = checkInvalidUserName(userName);

        // 패스워트 확인
        if(!passwordEncoder.matches(password, userAccount.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰생성
        String token = jwtTokenUtils.generateToken(userName);


        return token;
    }

    public UserDto loadUserByUserName(String userName) {
        return userEntityRepository.findByUserName(userName)
                .map(UserDto::fromEntity)
                .orElseThrow(() ->
                        new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    public Page<AlarmDto> alarmList(String userName, Pageable pageable) {
        UserAccount userAccount = checkInvalidUserName(userName);
        return alarmRepository.findByUser(userAccount, pageable)
                .map(AlarmDto::fromEntity);
    }


    private UserAccount checkInvalidUserName(String userName){
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
