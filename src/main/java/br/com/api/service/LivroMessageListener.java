package br.com.api.service;

import br.com.api.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class LivroMessageListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void mensagem(String mensagem) {
        System.out.println("############################ TESTEEEEEEE #############################\n" + mensagem);
    }
}
