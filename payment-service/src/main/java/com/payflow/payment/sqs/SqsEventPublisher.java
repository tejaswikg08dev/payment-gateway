package com.payflow.payment.sqs;

import com.payflow.common.event.PaymentEvent;
import com.payflow.payment.service.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("aws")
@Slf4j
public class SqsEventPublisher implements EventPublisher {
    @Override
    public void publishPaymentEvent(String topic, PaymentEvent event) {
        log.info("SQS: Publishing event to queue [{}]: eventId={}, paymentId={}",
                topic, event.getEventId(), event.getPaymentId());
        // TODO: Implement AWS SQS publishing
        // SqsClient sqsClient = ...
        // SendMessageRequest sendRequest = SendMessageRequest.builder()
        //         .queueUrl(resolveQueueUrl(topic))
        //         .messageBody(objectMapper.writeValueAsString(event))
        //         .messageGroupId(event.getMerchantId())
        //         .messageDeduplicationId(event.getEventId())
        //         .build();
        // sqsClient.sendMessage(sendRequest);
        log.warn("SQS EventPublisher is a stub — event not actually sent");
    }
}
