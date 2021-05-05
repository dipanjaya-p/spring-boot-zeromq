package com.boot.sseemitter.config;

import io.insource.framework.annotation.EnableZmqPublisher;
import io.insource.framework.zeromq.ZmqTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.integration.zeromq.channel.ZeroMqChannel;
import org.springframework.integration.zeromq.inbound.ZeroMqMessageProducer;
import org.springframework.messaging.MessageChannel;
import org.zeromq.SocketType;
import org.zeromq.ZContext;

import java.time.Duration;

@Configuration
public class ZeroMQConfig {

   /* @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ZmqTemplate zmqTemplate() {
        return new ZmqTemplate();
    }*/

    @Bean(destroyMethod = "close")
    public ZContext zContext() {
        return new ZContext();
    }

   /* @Bean
    public ZeroMqChannel zeroMqChannel(ZContext context) {

        ZeroMqChannel channel = new ZeroMqChannel(context, true);
        channel.setConnectUrl("tcp://localhost:5556");
        channel.setConsumeDelay(Duration.ofMillis(10));

        return channel;
    }*/

    @Bean
    public FluxMessageChannel fluxMessageChannel() {
        FluxMessageChannel messageChannel = new FluxMessageChannel();
        return messageChannel;
    }

    @Bean
    public ZeroMqMessageProducer zeroMqMessageProducer(ZContext context) {

        ZeroMqMessageProducer messageProducer = new ZeroMqMessageProducer(context, SocketType.SUB);
        messageProducer.setOutputChannel(fluxMessageChannel());
        messageProducer.setTopics("769042", "698001");
        messageProducer.setReceiveRaw(true);
        messageProducer.setConnectUrl("tcp://localhost:5556");
        messageProducer.setConsumeDelay(Duration.ofMillis(10));

        return messageProducer;
    }


}
