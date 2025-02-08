package br.com.dv.qrcodeapi.exception;

public class InvalidContentException extends RuntimeException {

    private static final String INVALID_CONTENT_MESSAGE = "Contents cannot be null or blank";

    public InvalidContentException() {
        super(INVALID_CONTENT_MESSAGE);
    }

}
