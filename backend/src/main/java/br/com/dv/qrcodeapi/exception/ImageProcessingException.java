package br.com.dv.qrcodeapi.exception;

public class ImageProcessingException extends RuntimeException {

    private static final String IMAGE_PROCESSING_ERROR_MESSAGE = "Error during image processing";

    public ImageProcessingException(Throwable cause) {
        super(IMAGE_PROCESSING_ERROR_MESSAGE, cause);
    }

}
