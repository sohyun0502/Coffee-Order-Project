package kr.spartaclub.coffeeorderproject.domain.order.producer;

import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderCompletedEvent> orderCompletedEventKafkaTemplate;

    public void send(OrderCompletedEvent event) {
        orderCompletedEventKafkaTemplate.send("order-completed", event);
    }
}
