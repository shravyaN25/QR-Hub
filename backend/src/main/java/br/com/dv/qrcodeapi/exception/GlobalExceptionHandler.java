package br.com.dv.qrcodeapi.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatchException(TypeMismatchException e) {
        String errorMessage = String.format(
                "Invalid parameter value '%s' for '%s'",
                e.getValue(),
                e.getPropertyName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        if (errorMessage.isEmpty()) {
            errorMessage = "Validation error";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = "Invalid request body: " + e.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(errorMessage));
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ApiError> handleImageProcessingException(ImageProcessingException e) {
        return getResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            InvalidImageSizeException.class,
            InvalidImageFormatException.class,
            InvalidContentException.class,
            InvalidCorrectionLevelException.class,
            InvalidColorException.class,
            InvalidMarginException.class
    })
    public ResponseEntity<ApiError> handleAllBadRequestExceptions(Exception e) {
        return getResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return getResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return getResponseEntity(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(QRCodeNotFoundException.class)
    public ResponseEntity<ApiError> handleQrCodeNotFoundException(QRCodeNotFoundException e) {
        return getResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiError> getResponseEntity(Exception e, HttpStatus status) {
        ApiError error = new ApiError(e.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    public record ApiError(String error) {}

}
