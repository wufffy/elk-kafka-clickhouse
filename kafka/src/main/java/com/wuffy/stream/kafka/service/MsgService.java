package com.wuffy.stream.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;

/**
 * @author wuffy
 * @version 1.0
 * @date 2019-04-02 15:59
 * @Description TODO
 */
@Service
@Slf4j
public class MsgService {
    @Autowired
    private Producer<String, String> producer;
    @Autowired
    private Consumer<String, String> consumer;

    void send(String topic, String value) {
        producer.send(new ProducerRecord<String, String>(topic, value));
    }

    void consumer(String topic) {
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                log.info("topic={},value={}", record.topic(), record.value());
            }
        }
    }

    /**
     * 服务启动 开始消费001 的topic
     */
    @PostConstruct
    public void sendMsg() {
        consumer("001");
    }

}
