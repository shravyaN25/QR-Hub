package br.com.dv.qrcodeapi.exception;

import java.util.UUID;

public class QRCodeNotFoundException extends RuntimeException {

    private static final String QR_CODE_NOT_FOUND_MESSAGE_TEMPLATE = "QR code with ID '%s' not found";

    public QRCodeNotFoundException(UUID id) {
        super(String.format(QR_CODE_NOT_FOUND_MESSAGE_TEMPLATE, id));
    }

}
