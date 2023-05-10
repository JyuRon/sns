package com.example.repository;

import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {

    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(UserDto userDto){
        String key = getKey(userDto.getUsername());
        log.info("Set User to Redis {}:{}", key, userDto);
        userDtoRedisTemplate.opsForValue().set(key, userDto, USER_CACHE_TTL);
    }

    public Optional<UserDto> getUser(String userName){
        String key = getKey(userName);
        UserDto userDto = userDtoRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {}:{}", key, userDto);
        return Optional.ofNullable(userDto);
    }

    public String getKey(String userName){
        return "USER:" + userName;
    }

}
