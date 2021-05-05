package com.boot.sseemitter.controller;

import com.boot.sseemitter.model.Flight;
import com.boot.sseemitter.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 *
 */

@Slf4j
@RestController
public class FlightController {

    @Autowired
    @Qualifier("fixedThreadPool")
    protected ExecutorService executor;

    @Autowired
    protected FlightService service;

    //    @Autowired
    protected List<SseEmitter> emitters;

    @Autowired
    protected Map<String, SseEmitter> emitterMap;


    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("NEWFLHT"));
            sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        } catch (IOException e) {
            log.info("Exception arise while subscribe " + e.getCause());
            sseEmitter.completeWithError(e);
        }
        emitters.add(sseEmitter);
        return sseEmitter;
    }

    /**   Handler for dispatching events to all the clients*/
    @PostMapping(value = "/dispatch/{userId}/{eventName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void dispatchEvent(@RequestBody Flight flight, @PathVariable("eventName") String eventName, @PathVariable("userId") String userId) {
//        emitters.forEach(e -> {
        if (emitterMap.containsKey(userId)) {
            SseEmitter emitter = emitterMap.get(userId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(flight));
                } catch (IOException io) {
                    log.info("Exception during dispatching events");
                    emitter.completeWithError(io);
                }
            }
        }

//        });
    }

 /**Client subscription handler*/
    @CrossOrigin
    @GetMapping("/{userId}/{event}")
    public SseEmitter fetchFlightDataAndSubscribe(@PathVariable("event") String eventName, @PathVariable String userId) {
        log.info("Emitting flight data sets");
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
//        executor.execute(() -> {
        Runnable run = () -> {
            List<Flight> flights = service.getFlights();

            /*try {
                service.getFlights().stream().forEach(f -> {
                    try {
                        randomDelay();
                        sseEmitter.send(f);
                    } catch (IOException io) {
                        log.info("Exception arise in foreach stream while emitting flight datasets|| reason :: " + io.getCause());
                    }
                });
                sseEmitter.send(SseEmitter.event().name(eventName));
                emitterMap.put(userId, sseEmitter);
            } catch (IOException e) {
                log.info("Exception arise while emitting flight data, reason :: " + e.getCause());
                emitterMap.remove(userId);
                sseEmitter.completeWithError(e);
                log.info("--------------------------------------------------");
            } finally {
                sseEmitter.onCompletion(() -> emitterMap.remove(userId));
                sseEmitter.onTimeout(() -> emitterMap.remove(userId));
                sseEmitter.onError((i -> emitterMap.remove(userId)));
            }*/

            try {
                for (Flight fl : flights) {
                    randomDelay();
                    sseEmitter.send(fl);
                }

                sseEmitter.send(SseEmitter.event().name(eventName));
                emitterMap.put(userId, sseEmitter);

            } catch (IOException e) {
                log.info("Exception arise while emitting flight data, reason :: " + e.getCause());
                emitterMap.remove(userId);
                sseEmitter.completeWithError(e);
                log.info("----------------------------------------");
            } finally {
                sseEmitter.onCompletion(() -> emitterMap.remove(userId));
                sseEmitter.onTimeout(() -> emitterMap.remove(userId));
                sseEmitter.onError((i -> emitterMap.remove(userId)));
            }
        };
        new Thread(run).start();
//        });
//        executor.shutdown();
        log.info("executor shutdown completed");
        return sseEmitter;
    }


    private void randomDelay() {
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException ie) {
            log.info("Thread is interrupted");
            Thread.currentThread().interrupt();
        }
    }

}
