package com.example.repository;

import com.example.AbstractRedisContainer;
import com.example.constant.UserRole;
import com.example.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserCacheRepositoryTest extends AbstractRedisContainer {

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private RedisTemplate<String, UserDto> userDtoRedisTemplate;


    @DisplayName("Redis UserDto insert 테스트")
    @Test
    void givenUserDtoData_whenSetData_thenSelectData(){
        // Given
        UserDto userDto = createUserDto("jyuka");

        // When
        userCacheRepository.setUser(userDto);

        // Then
        UserDto result = userDtoRedisTemplate.opsForValue().get(userCacheRepository.getKey(userDto.getUsername()));
        assertThat(result.getId()).isEqualTo(userDto.getId());
        assertThat(result.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(result.getPassword()).isEqualTo(userDto.getPassword());
        assertThat(result.getUserRole()).isEqualTo(userDto.getUserRole());
        assertThat(result.getRegisterAt()).isEqualTo(userDto.getRegisterAt());
        assertThat(result.getUpdatedAt()).isEqualTo(userDto.getUpdatedAt());
        assertThat(result.getDeletedAt()).isEqualTo(userDto.getDeletedAt());
    }

    @DisplayName("Redis UserDto select 테스트")
    @Test
    void givenUserDtoData_whenGetData_thenSelectData(){
        // Given
        UserDto userDto = createUserDto("jyuka2");
        userDtoRedisTemplate.opsForValue().set(userCacheRepository.getKey(userDto.getUsername()), userDto);

        // When
        UserDto result = userCacheRepository.getUser(userDto.getUsername())
                .orElse(null);

        // Then
        assertThat(result.getId()).isEqualTo(userDto.getId());
        assertThat(result.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(result.getPassword()).isEqualTo(userDto.getPassword());
        assertThat(result.getUserRole()).isEqualTo(userDto.getUserRole());
        assertThat(result.getRegisterAt()).isEqualTo(userDto.getRegisterAt());
        assertThat(result.getUpdatedAt()).isEqualTo(userDto.getUpdatedAt());
        assertThat(result.getDeletedAt()).isEqualTo(userDto.getDeletedAt());
    }

    @DisplayName("Redis Key 설정 확인")
    @Test
    void givenUserName_whenAddUStringUser_thenCheckString(){
        // Given
        String userName="jyuka";

        // When
        String key = userCacheRepository.getKey(userName);

        // Then
        assertThat(key).isEqualTo("USER:"+userName);
    }

    private UserDto createUserDto(String userName){
        return UserDto.of(
                1L,
                userName,
                "1234",
                UserRole.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

}