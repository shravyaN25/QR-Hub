package br.com.dv.qrcodeapi.exception;

public class InvalidMarginException extends RuntimeException {

    private static final String INVALID_MARGIN_MESSAGE = "Margin must be between 0 and 50 pixels";

    public InvalidMarginException() {
        super(INVALID_MARGIN_MESSAGE);
    }

}
