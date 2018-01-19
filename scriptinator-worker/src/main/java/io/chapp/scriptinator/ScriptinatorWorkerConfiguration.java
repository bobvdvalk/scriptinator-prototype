package io.chapp.scriptinator;

import io.chapp.scriptinator.services.MessageReceiver;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.script.ScriptEngine;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@ConfigurationProperties("haphus")
public class ScriptinatorWorkerConfiguration {
    private String workerName;
    private int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    private int maxPoolSize = Integer.MAX_VALUE;
    private String workspace = "data/workspace";


    public String getWorkerName() {
        if (workerName == null) {
            try {
                workerName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new IllegalStateException("Could not determine hostname", e);
            }
        }
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageReceiver messageReceiver) {
        return new MessageListenerAdapter(messageReceiver, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer container(Queue queue, ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue.getName());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    NashornScriptEngineFactory engineFactory() {
        return new NashornScriptEngineFactory();
    }

    @Bean
    @Scope("prototype")
    ScriptEngine scriptEngine() {
        return engineFactory().getScriptEngine();
    }

}
