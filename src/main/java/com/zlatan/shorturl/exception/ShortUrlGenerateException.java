package com.zlatan.shorturl.exception;

/**
 * Created by Zlatan on 19/2/24.
 */
public class ShortUrlGenerateException extends RuntimeException {

    public ShortUrlGenerateException() {
        super();
    }

    public ShortUrlGenerateException(String message) {
        super(message);
    }
}
