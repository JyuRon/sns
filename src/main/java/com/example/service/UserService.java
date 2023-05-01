package com.example.service;

import com.example.constant.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.dto.UserDto;
import com.example.domain.UserAccount;
import com.example.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDto join(String userName, String password){
        userEntityRepository.findByUserName(userName)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                })
        ;

        UserAccount userAccount = userEntityRepository.save(UserAccount.of(userName, passwordEncoder.encode(password)));

        return UserDto.fromEntity(userAccount);
    }

}
