package br.com.dv.qrcodeapi.validation;

import br.com.dv.qrcodeapi.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QRCodeParameterValidatorTest {

    private QRCodeParameterValidator qrCodeParameterValidator;

    @BeforeEach
    void setUp() {
        qrCodeParameterValidator = new QRCodeParameterValidator();
    }

    @Test
    @DisplayName("Should successfully validate QR code parameters when all inputs are valid")
    void shouldValidateWithValidParameters() {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("Should throw InvalidContentException when content is null, empty, or blank")
    void shouldThrowExceptionForInvalidContent(String content) {
        assertThrows(InvalidContentException.class, () -> qrCodeParameterValidator.validate(
                content, 250, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 50, 149, 351, 500})
    @DisplayName("Should throw InvalidImageSizeException when size is outside allowed range")
    void shouldThrowExceptionForInvalidSize(int size) {
        assertThrows(InvalidImageSizeException.class, () -> qrCodeParameterValidator.validate(
                "content", size, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(ints = {150, 200, 250, 300, 350})
    @DisplayName("Should successfully validate QR code parameters when size is within allowed range")
    void shouldValidateWithValidSize(int size) {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", size, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"l", "m", "q", "h", "L", "M", "Q", "H"})
    @DisplayName("Should successfully validate QR code parameters when correction level is valid")
    void shouldValidateWithValidCorrectionLevel(String correction) {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", 250, correction, "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "X", "Y", "invalid", "1", "LOW", "MEDIUM"})
    @DisplayName("Should throw InvalidCorrectionLevelException when correction level is not L, M, Q, or H")
    void shouldThrowExceptionForInvalidCorrectionLevel(String correction) {
        assertThrows(InvalidCorrectionLevelException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, correction, "png", "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"png", "PNG", "jpeg", "JPEG", "gif", "GIF"})
    @DisplayName("Should successfully validate QR code parameters when format is supported")
    void shouldValidateWithValidFormat(String format) {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", 250, "L", format, "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"bmp", "tiff", "webp", "svg", "raw", ""})
    @DisplayName("Should throw InvalidImageFormatException when format is not supported")
    void shouldThrowExceptionForInvalidFormat(String format) {
        assertThrows(InvalidImageFormatException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, "L", format, "#000000", "#FFFFFF", 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"#000000", "BLACK", "RED", "#FFFFFF", "BLUE", "#00FF00"})
    @DisplayName("Should successfully validate QR code parameters when color format is valid")
    void shouldValidateWithValidColor(String color) {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", color, "#FFFFFF", 4
        ));
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", "#000000", color, 4
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RANDOM", "RAINBOW", "PINK", "#12345", "#XYZABC"})
    @DisplayName("Should throw InvalidColorException when color format is not valid")
    void shouldThrowExceptionForInvalidColor(String color) {
        assertThrows(InvalidColorException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", color, "#FFFFFF", 4
        ));
        assertThrows(InvalidColorException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", "#000000", color, 4
        ));
    }

    @Test
    @DisplayName("Should successfully validate QR code parameters when content contains special characters")
    void shouldValidateWithSpecialCharacters() {
        String specialContent = "Hello! こんにちは! ❤️ #@$%";
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                specialContent, 250, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @Test
    @DisplayName("Should successfully validate QR code parameters when content exceeds typical length")
    void shouldValidateWithLongContent() {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                "a".repeat(1000), 250, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @Test
    @DisplayName("Should successfully validate QR code parameters when content is a URL")
    void shouldValidateWithUrlContent() {
        String urlContent = "https://example.com/path?param1=value1&param2=value2";
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                urlContent, 250, "L", "png", "#000000", "#FFFFFF", 4
        ));
    }

    @Test
    @DisplayName("Should throw InvalidMarginException when margin is outside allowed range")
    void shouldThrowExceptionForInvalidMargin() {
        assertThrows(InvalidMarginException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", "#000000", "#FFFFFF", -1
        ));
        assertThrows(InvalidMarginException.class, () -> qrCodeParameterValidator.validate(
                "content", 250, "L", "png", "#000000", "#FFFFFF", 51
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "content,250,L,png,#000000,#FFFFFF,4",
            "https://example.com,350,H,jpeg,RED,#FFFF00,0",
            "test123,150,M,jpeg,BLUE,#00FF00,10",
            "hello world,300,Q,gif,#808080,#AAAAAA,50"
    })
    @DisplayName("Should successfully validate QR code parameters with various valid combinations")
    void shouldValidateWithValidParameterCombinations(
            String content, int size, String correction, String format,
            String fcolor, String bcolor, int margin
    ) {
        assertDoesNotThrow(() -> qrCodeParameterValidator.validate(
                content, size, correction, format, fcolor, bcolor, margin
        ));
    }

}
