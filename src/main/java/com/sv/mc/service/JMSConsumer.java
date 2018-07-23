package com.sv.mc.service;

import com.sv.mc.conf.JmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class JMSConsumer {
    private final static Logger logger = LoggerFactory.getLogger(JMSConsumer.class);

    @JmsListener(destination = "youTopic",containerFactory = "jmsListenerContainerTopic")
    public void onTopicMessage(String msg) {
        logger.error("接收到topic消息：{}",msg);
    }

    @JmsListener(destination = JmsConfig.QUEUE,containerFactory = "jmsListenerContainerQueue")
    public void onQueueMessage(String msg) {
        logger.error("接收到queue消息：{}",msg);
    }
}