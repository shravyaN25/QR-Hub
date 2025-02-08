package br.com.dv.qrcodeapi.exception;

public class InvalidCorrectionLevelException extends RuntimeException {

    private static final String INVALID_CORRECTION_LEVEL_MESSAGE = "Permitted error correction levels are L, M, Q, H";

    public InvalidCorrectionLevelException() {
        super(INVALID_CORRECTION_LEVEL_MESSAGE);
    }

}
