package com.wuffy.stream.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wuffy
 * @version 1.0
 * @date 2019-04-02 13:59
 * @Description kafka配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "custom.kafka")
public class KafkaCustomProperties {
    /**
     * kafka的服务地址
     */
    private String bootstrapServers;
    private String keySerializer;
    private String keyDeserializer;
    private String valueSerializer;
    private String valueDeserializer;
    private String groupId;
}