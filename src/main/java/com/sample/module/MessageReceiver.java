package com.sample.module;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import javax.jms.Connection;
import javax.jms.Destination;

@Component
public class MessageReceiver {

    public static String url = "tcp://localhost:61616";

    public static String q = "queue1";
    ConnectionFactory connectionFactory;
    Connection connection;
    Session session;
    Destination destination;
    TextMessage message;
    MessageProducer messageProducer;
    MessageConsumer consumer;
    Message m;

    {
        try {
        connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(q);

        consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public String receiveMessage(){
        String text = null;
        try {
            m = consumer.receive();
            if(m!=null) {
                TextMessage m1 = (TextMessage) m;
                text = m1.getText();
            }
//            session.close();
//            connection.close();
//            consumer.close();

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return text;
    }
}
