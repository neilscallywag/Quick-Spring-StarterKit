/* (C)2024 */
package com.starterkit.demo.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.starterkit.demo.model.ClickstreamEvent;

@Service
public class ClickstreamEventProducer {

    private final KafkaTemplate<String, ClickstreamEvent> kafkaTemplate;
    private final String topic;

    public ClickstreamEventProducer(
            KafkaTemplate<String, ClickstreamEvent> kafkaTemplate,
            @Value("${kafka.topic.clickstream}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendEvent(ClickstreamEvent event) {
        kafkaTemplate.send(new ProducerRecord<>(topic, event.userId().toString(), event));
    }
}
