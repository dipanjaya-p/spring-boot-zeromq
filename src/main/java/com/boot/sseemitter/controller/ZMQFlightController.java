package com.boot.sseemitter.controller;

import com.boot.sseemitter.exceptions.SubscriptionFailedException;
import io.insource.framework.zeromq.ZmqTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.integration.zeromq.channel.ZeroMqChannel;
import org.springframework.integration.zeromq.inbound.ZeroMqMessageProducer;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zeromq.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/zmq")
public class ZMQFlightController {

    @Autowired
    protected ZContext context;

    @Autowired
    protected FluxMessageChannel outputChannel;

    @GetMapping("/{msg}")
    public ResponseEntity<?> fetchData(@PathVariable("msg") String message) {

        Properties response = new Properties();
        AtomicInteger count = new AtomicInteger(0);
        try (ZMQ.Socket req = context.createSocket(SocketType.REQ);) {
            req.connect("tcp://localhost:5555");

            IntStream.iterate(0, i -> i < 5, i -> i + 1).forEach(action -> {
                log.info("Sending request to HWServer " + message + "  " + count);
                byte[] res;

                if (req.send(message.getBytes(ZMQ.CHARSET), 0)) {
                    res = req.recv(0);
                    response.put(message + " : " + count, new String(res, ZMQ.CHARSET));
                }
                count.incrementAndGet();
            });

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAILED DUE TO " + e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }

    /**
     * Implementation using Zeromq
     */
    @GetMapping("/weather/{zip}")
    public SseEmitter subscribeWeatherDataByZipcode(@PathVariable("zip") String zipcode) {
        log.info("/weather/{zip}: End point");

        AtomicReference<String> response = new AtomicReference<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        executorService.execute(() -> {

            try {
                try (ZMQ.Socket subscriber = context.createSocket(SocketType.SUB)) {
                    subscriber.connect("tcp://localhost:5556");
                    while (true) {
                        response.set(subscriber.subscribe(zipcode.getBytes(ZMQ.CHARSET)) ? subscriber.recvStr(0) + " : " +
                                subscriber.recvStr(0) : HttpStatus.NOT_FOUND.toString());
                        emitter.send(SseEmitter.event().name(zipcode).data(response.get()));
                    }
                }
            } catch (ZMQException e) {
                log.info("Exception arise due to " + e.getMessage());
                throw new SubscriptionFailedException(e.getMessage(), e.getCause());
            } catch (Exception e) {
                log.info("Exception while emitting data through SseEmitter" + e.getMessage());
                emitter.completeWithError(e);
            } finally {
                log.info("complete on emitter");
                emitter.onCompletion(() -> emitter.complete());
            }
            log.info("Response from topic :: " + zipcode + " :: " + response.get());
        });
//        if (response.get() != null & emitter != null)
//            return ResponseEntity.ok().body(response.get());
//        else
        return emitter;
    }


    /**
     * Spring Integration Zeromq implementation
     */

    @GetMapping("/flux/{zip}")
    public SseEmitter fetchWeatherInfoByZipcode(@PathVariable("zip") String zipcode) {
        log.info("/flux/{zip}: End point");
        AtomicReference<String> response = new AtomicReference<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        executorService.execute(() -> {
            try {
                Flux<ZMsg> testFlux = Flux.from(outputChannel).map(Message::getPayload).cast(ZMsg.class);
                testFlux.subscribe(msg -> {
                    response.set(msg.toString());

                    try {
                        emitter.send(SseEmitter.event().name(zipcode).data(response.get()));
                    } catch (IOException e) {
                        log.info("Exception while emitting data through SseEmitter" + e.getMessage());
                        emitter.completeWithError(e);
                    } finally {
                        log.info("complete on emitter");
                        emitter.onCompletion(() -> emitter.complete());
                    }
                });

            } catch (Exception e) {
                log.info("Exception arise due to channel failure");
            }
        });
        return emitter;
    }

}
