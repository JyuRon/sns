package com.example.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EmitterRepositoryTest {

    @InjectMocks private EmitterRepository emitterRepository;

    @BeforeEach
    void clear(){
        emitterRepository.getEmitterMap().clear();
    }

    @DisplayName("유저 아이디와 sseEmiter를 받아 로컬 캐시에 저장한다.")
    @Test
    void givenUserIdAndEmitter_whenSaveEmitter_thenReturnSuccess(){
        // Given
        Long userId = 1L;
        SseEmitter emitter = new SseEmitter(30000L);

        // When
        SseEmitter result = emitterRepository.save(userId, emitter);

        // Then
        assertThat(result.getTimeout()).isEqualTo(emitter.getTimeout());
    }

    @DisplayName("유저 아이디를 받아 로컬 캐시에 저장된 emitter 를 불러온다.")
    @Test
    void givenUserId_whenGetEmitter_thenReturnSuccess(){
        // Given
        Long userId = 1L;
        SseEmitter emitter = new SseEmitter(30000L);
        emitterRepository.save(userId, emitter);

        // When
        SseEmitter result = emitterRepository.get(userId).get();

        // Then
        assertThat(result.getTimeout()).isEqualTo(emitter.getTimeout());
    }

    @DisplayName("유저 아이디를 받아 로컬 캐시에 저장된 emitter 를 삭제한다.")
    @Test
    void givenUserId_whenDeleteEmitter_thenReturnSuccess(){
        // Given
        Long userId = 1L;
        SseEmitter emitter = new SseEmitter();
        emitterRepository.save(userId, new SseEmitter());

        // When
        emitterRepository.delete(userId);

        // Then
        Optional<SseEmitter> result = emitterRepository.get(1L);
        assertThat(result).isEmpty();
    }

    @DisplayName("userId를 받아 커스텀된 키값으로 변환하다.")
    @Test
    void givenUserId_whenCustomKey_thenCustomValue(){
        // Given
        Long userId = 1L;

        // When
        String result = emitterRepository.getKey(userId);

        // Then
        assertThat(result).isEqualTo("EMITTER:UID:"+userId);
    }

}