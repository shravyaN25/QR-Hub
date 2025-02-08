package br.com.dv.qrcodeapi.exception;

public class InvalidImageSizeException extends RuntimeException {

    private static final String INVALID_IMAGE_SIZE_MESSAGE = "Image size must be between 150 and 350 pixels";

    public InvalidImageSizeException() {
        super(INVALID_IMAGE_SIZE_MESSAGE);
    }

}
