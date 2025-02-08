package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.exception.InvalidColorException;
import br.com.dv.qrcodeapi.exception.InvalidCorrectionLevelException;
import br.com.dv.qrcodeapi.exception.InvalidMarginException;
import br.com.dv.qrcodeapi.validation.QRCodeParameterValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class QRCodeGenerationServiceTest {

    @Mock
    private QRCodeParameterValidator qrCodeParameterValidator;

    private QRCodeGenerationService qrCodeGenerationService;

    @BeforeEach
    void setUp() {
        qrCodeGenerationService = new QRCodeGenerationServiceImpl(qrCodeParameterValidator);
    }

    @Test
    @DisplayName("Should successfully generate QR code image when all parameters are valid")
    void shouldGenerateQRCodeWithValidParameters() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
        assertEquals(MediaType.IMAGE_PNG, qrCode.mediaType());
    }

    @ParameterizedTest
    @CsvSource({
            "L,png,#000000,#FFFFFF,4,image/png",
            "M,jpeg,RED,WHITE,10,image/jpeg",
            "Q,gif,BLUE,#FFFF00,0,image/gif",
            "H,png,#808080,#0000FF,50,image/png"
    })
    @DisplayName("Should generate QR code with correct media type for different format combinations")
    void shouldGenerateQRCodeWithCorrectMediaType(
            String correction, String format, String fcolor, String bcolor, int margin, String expectedMediaType) {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, correction, format,
                fcolor, bcolor, margin
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, correction, format,
                fcolor, bcolor, margin
        );

        assertNotNull(qrCode);
        assertTrue(qrCode.imageData().length > 0);
        assertEquals(MediaType.parseMediaType(expectedMediaType), qrCode.mediaType());
    }

    @Test
    @DisplayName("Should throw InvalidCorrectionLevelException when correction level is not valid")
    void shouldThrowExceptionForInvalidCorrectionLevel() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "invalid", "png",
                "#000000", "#FFFFFF", 4
        );

        assertThrows(InvalidCorrectionLevelException.class, () -> qrCodeGenerationService.generateQRCode(
                "content", 250, "invalid", "png",
                "#000000", "#FFFFFF", 4)
        );
    }

    @Test
    @DisplayName("Should throw InvalidColorException when color format is not valid")
    void shouldThrowExceptionForInvalidColor() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "INVALID", "#FFFFFF", 4
        );

        assertThrows(InvalidColorException.class, () -> qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "INVALID", "#FFFFFF", 4)
        );
    }

    @Test
    @DisplayName("Should throw InvalidMarginException when margin value is out of allowed range")
    void shouldThrowExceptionForInvalidMargin() {
        doThrow(new InvalidMarginException()).when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 999
        );

        assertThrows(InvalidMarginException.class, () -> qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 999)
        );
    }

    @Test
    @DisplayName("Should successfully generate QR code with minimum allowed size")
    void shouldGenerateQRCodeWithMinimumSize() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 150, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 150, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code with maximum allowed size")
    void shouldGenerateQRCodeWithMaximumSize() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 350, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 350, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code with named colors")
    void shouldGenerateQRCodeWithNamedColors() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "BLACK", "WHITE", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "BLACK", "WHITE", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code with hex color codes")
    void shouldGenerateQRCodeWithHexColors() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "#FF0000", "#00FF00", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "#FF0000", "#00FF00", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code with minimum margin value")
    void shouldGenerateQRCodeWithMinimumMargin() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 0
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 0
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code with maximum margin value")
    void shouldGenerateQRCodeWithMaximumMargin() {
        doNothing().when(qrCodeParameterValidator).validate(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 50
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                "content", 250, "L", "png",
                "#000000", "#FFFFFF", 50
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code when content exceeds typical length")
    void shouldGenerateQRCodeWithLongContent() {
        String longContent = "a".repeat(1000);
        doNothing().when(qrCodeParameterValidator).validate(
                longContent, 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                longContent, 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

    @Test
    @DisplayName("Should successfully generate QR code when content contains special characters")
    void shouldGenerateQRCodeWithSpecialCharacters() {
        String specialContent = "Hello! こんにちは! ❤️ #@$%";
        doNothing().when(qrCodeParameterValidator).validate(
                specialContent, 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        var qrCode = qrCodeGenerationService.generateQRCode(
                specialContent, 250, "L", "png",
                "#000000", "#FFFFFF", 4
        );

        assertNotNull(qrCode);
        assertNotNull(qrCode.imageData());
        assertTrue(qrCode.imageData().length > 0);
    }

}
