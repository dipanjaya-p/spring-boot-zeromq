package com.boot.sseemitter.exceptionMapper;

import com.boot.sseemitter.exceptions.SubscriptionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ZMQRestExceptionMapper {

    @ExceptionHandler(value = SubscriptionFailedException.class)
    public ResponseEntity<?> handleSubscriptionFailedException(SubscriptionFailedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


}
