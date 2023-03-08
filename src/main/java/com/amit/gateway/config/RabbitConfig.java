package com.amit.gateway.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Confguration for the service to connect to RabbitMQ on startup.
 */
@Configuration
public class RabbitConfig {

    /**
     * Queue to place order to order-service
     */
    @Value("${queue.name}")
    private String exportQueue;

    /**
     * Queue to receive status for orders placed
     */
    @Value("${status.queue.name}")
    private String statusQueue;

    /**
     * Initialize the order placement Rabbit queue
     */
    @Bean(name = "exportQueue")
    public Queue exportQueue() {
        return new Queue(exportQueue, true);
    }

    /**
     * Initialize the Rabbit queue to receive the status
     */
    @Bean(name = "statusQueue")
    public Queue statusQueue() {
        return new Queue(statusQueue, true);
    }

    /**
     * Initialize the {@link RabbitTemplate} to connect to Rabbit
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Create specific convertor to be used for RabbitMQ interactions
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
