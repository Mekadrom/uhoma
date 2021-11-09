package com.higgs.server.kafka;

import com.higgs.server.kafka.handle.Handler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HAKafkaConsumer {
    private final List<? extends Handler> handlers;

//    @KafkaListener(topics = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
//    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
//        this.handlers.forEach(it -> it.handle(headers, message));
//    }
}
