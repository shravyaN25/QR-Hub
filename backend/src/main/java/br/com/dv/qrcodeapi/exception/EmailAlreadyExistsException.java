package br.com.dv.qrcodeapi.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    private static final String EMAIL_ALREADY_EXISTS_MESSAGE_TEMPLATE = "Email '%s' already exists";

    public EmailAlreadyExistsException(String email) {
        super(String.format(EMAIL_ALREADY_EXISTS_MESSAGE_TEMPLATE, email));
    }

}
