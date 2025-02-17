package com.tulio.authservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {
    private final JmsTemplate jmsTemplate;

    public MessageProducerService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String operation, String message, String email, boolean isSuccess) {
    	Map<String, Object> messagePayload = new HashMap<>();
    	messagePayload.put("message", message);
        messagePayload.put("timestamp", LocalDateTime.now().toString());
        messagePayload.put("email", email);
        messagePayload.put("status", isSuccess ? "success" : "failed"); 
        jmsTemplate.convertAndSend(operation, messagePayload);
    }
}
