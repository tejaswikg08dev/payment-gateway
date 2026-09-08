package com.payflow.payment.service;

import com.payflow.common.event.PaymentEvent;

public interface EventPublisher {

    void publishPaymentEvent(String topic, PaymentEvent event);
}
