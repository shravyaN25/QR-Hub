package br.com.dv.qrcodeapi.exception;

public class InvalidCredentialsException extends RuntimeException {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    public InvalidCredentialsException() {
        super(INVALID_CREDENTIALS_MESSAGE);
    }

}
