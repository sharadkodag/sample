package com.sample.module;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class MessageSender {

    public static String url = "tcp://localhost:61616";

    public static String q = "queue";
    ConnectionFactory connectionFactory;
    Connection connection;
    Session session;
    Destination destination;
    TextMessage message;
    MessageProducer messageProducer;
    MessageConsumer consumer;
    {

        connectionFactory = new ActiveMQConnectionFactory(url);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(q);
            messageProducer = session.createProducer(destination);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(String m, String q) throws JMSException {
        destination = session.createQueue(q);
        message = session.createTextMessage(m);
        messageProducer.send(message);
//        session.close();
//        connection.close();
//        consumer.close();
    }

}
