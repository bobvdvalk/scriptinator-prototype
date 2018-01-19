package io.chapp.scriptinator;

import io.chapp.scriptinator.model.Job;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    Queue queue() {
        return new Queue(Job.NEW_JOB_QUEUE, false);
    }

    @Bean
    Exchange exchange() {
        return new TopicExchange("scriptinator");
    }
}
