package com.wuffy.stream.kafka.config;

import com.wuffy.stream.kafka.properties.KafkaCustomProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author wuffy
 * @version 1.0
 * @date 2019-04-02 16:34
 * @Description kafka的配置类
 */
@Configuration
public class KafkaConfig {

    private KafkaCustomProperties kafkaProperties;

    public KafkaConfig(KafkaCustomProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Producer<String, String> getProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProperties.getBootstrapServers());
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", kafkaProperties.getKeySerializer());
        props.put("value.serializer", kafkaProperties.getValueSerializer());
        return new KafkaProducer<>(props);
    }

    @Bean
    public Consumer<String, String> getConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProperties.getBootstrapServers());
        props.put("group.id", kafkaProperties.getGroupId());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", kafkaProperties.getKeyDeserializer());
        props.put("value.deserializer", kafkaProperties.getValueDeserializer());
        return new KafkaConsumer<>(props);
    }
}
