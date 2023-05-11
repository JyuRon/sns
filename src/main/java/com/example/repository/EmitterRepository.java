package com.example.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class EmitterRepository {

    @Getter
    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Long userId, SseEmitter sseEmitter){
        final String key = getKey(userId);
        emitterMap.put(key, sseEmitter);
        log.info("Set sseEmitter {}", key);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Long userId){
        final String key = getKey(userId);
        log.info("Get sseEmitter {}", key);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(Long userId){
        emitterMap.remove(getKey(userId));
    }
    public String getKey(Long userId){
        return "EMITTER:UID:" + userId;
    }
}
