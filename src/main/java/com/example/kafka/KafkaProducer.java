package com.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, AlarmEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private static final String ALARM_TOPIC = "alarm";


    public void sendMessage(AlarmEvent alarmEvent) {
        kafkaTemplate.send(ALARM_TOPIC, alarmEvent.getReceiveUserID().toString(), alarmEvent);
        log.info("Kafka Producer Send Finish");
    }
}