package br.com.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "livroExchange";
    public static final String QUEUE_NAME = "livroQueue";
    public static final String ROUTING_KEY = "livro.key";

    @Bean
    public TopicExchange livroExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue livroQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue livroQueue, TopicExchange livroExchange) {
        return BindingBuilder.bind(livroQueue)
                .to(livroExchange)
                .with(ROUTING_KEY);
    }
}
