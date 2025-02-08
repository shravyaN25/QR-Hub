package br.com.dv.qrcodeapi.exception;

public class InvalidColorException extends RuntimeException {

    private static final String INVALID_COLOR_MESSAGE = "Color must be a valid color name or hex code (#RRGGBB)";

    public InvalidColorException() {
        super(INVALID_COLOR_MESSAGE);
    }

}
