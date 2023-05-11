package com.example.kafka;

import com.example.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AlarmService alarmService;

    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "foo")
    public void consume(AlarmEvent event, Acknowledgment ack) throws IOException {
        log.info("Kafka Consumer event : {}", event.toString());

        alarmService.send(event);
        // 메시지 처리 완료를 알림
        ack.acknowledge();
    }
}
