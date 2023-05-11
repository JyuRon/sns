package com.example;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
public abstract class AbstractRedisContainer {
    static final GenericContainer MY_REDIS_CONTAINER;
    static KafkaContainer MY_KAFKA_CONTAINER;


    static{
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379);

        MY_REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host",MY_REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port",MY_REDIS_CONTAINER.getMappedPort(6379).toString());

        MY_KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
    }

}
