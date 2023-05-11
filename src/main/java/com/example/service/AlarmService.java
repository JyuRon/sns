package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.Alarm;
import com.example.domain.UserAccount;
import com.example.exception.SnsApplicationException;
import com.example.kafka.AlarmEvent;
import com.example.repository.AlarmRepository;
import com.example.repository.EmitterRepository;
import com.example.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlarmService {

    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;
    private final UserAccountRepository userAccountRepository;
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";

    public SseEmitter connectAlarm(Long userId){
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        // 종료된 뒤
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));

        // 타임아웃 된 이후
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try{
            // 이벤트를 전달, client 에게 연결이 되었다는 것을 전달
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
            log.info("connect sse : {}", userId);
        }catch (IOException e){
            emitterRepository.delete(userId);
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;

    }

    public void send(AlarmEvent event) {
        UserAccount userAccount = userAccountRepository.findById(event.getReceiveUserID())
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        // only SSE send
        Alarm alarm = alarmRepository.save(
                Alarm.of(
                        userAccount,
                        event.getAlarmType(),
                        event.getAlarmArgs()
                )
        );
        emitterRepository.get(event.getReceiveUserID()).ifPresentOrElse(sseEmitter -> {
            try{
                sseEmitter.send(SseEmitter.event().id(alarm.getId().toString()).name(ALARM_NAME).data("new alarm"));
            }catch (IOException e){
                e.printStackTrace();
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }
}
