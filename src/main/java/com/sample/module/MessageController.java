package com.sample.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageController {
    @Autowired
    MessageListener messageListener;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage( String message) {
        messageListener.onMessage(message);
        jmsTemplate.convertAndSend("sample.queue", message);
    }
}
