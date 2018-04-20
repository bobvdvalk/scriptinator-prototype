/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.workerservices.MessageReceiver;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQReceiverConfiguration {
    private final WorkerConfiguration workerConfiguration;

    public RabbitMQReceiverConfiguration(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    MessageListenerAdapter executeJobListenerAdapter(MessageReceiver messageReceiver, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(messageReceiver, "executeJob");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter executeJobListenerAdapter) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Job.EXECUTE_JOB_QUEUE);
        container.setMessageListener(executeJobListenerAdapter);
        container.setConcurrentConsumers(workerConfiguration.getWorkers());

        return container;
    }
}
