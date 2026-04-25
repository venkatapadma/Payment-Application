package com.paypal.transaction_service.kafka;

import com.paypal.transaction_service.dto.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";

    private final KafkaTemplate<String, TransactionResponse> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, TransactionResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(String key, TransactionResponse transaction) {
        log.info("Sending to kafka -> Topic: {} Key: {} Message: {}", TOPIC, key, transaction);
        CompletableFuture<SendResult<String, TransactionResponse>> future = kafkaTemplate.send(TOPIC, key, transaction);

        future.thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("kafka message sent successfully! Topic: {}, Partition: {}, offset: {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        }).exceptionally((ex -> {
            log.error("Failed to send kafka message: {}", ex.getMessage());
            return null;
        }));
    }
}
