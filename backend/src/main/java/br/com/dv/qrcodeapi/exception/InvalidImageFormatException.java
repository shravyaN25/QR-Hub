package br.com.dv.qrcodeapi.exception;

public class InvalidImageFormatException extends RuntimeException {

    private static final String INVALID_IMAGE_FORMAT_MESSAGE = "Only png, jpeg and gif image types are supported";

    public InvalidImageFormatException() {
        super(INVALID_IMAGE_FORMAT_MESSAGE);
    }

}
