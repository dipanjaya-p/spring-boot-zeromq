package com.boot.sseemitter.exceptions;

public class SubscriptionFailedException extends RuntimeException{

    public SubscriptionFailedException(String message) {
        super(message);
    }

    public SubscriptionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionFailedException(Throwable cause) {
        super(cause);
    }

    protected SubscriptionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SubscriptionFailedException() {
        super();
    }
}
