package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.ImageResponse;
import br.com.dv.qrcodeapi.exception.InvalidColorException;
import br.com.dv.qrcodeapi.exception.InvalidContentException;
import br.com.dv.qrcodeapi.exception.InvalidCorrectionLevelException;
import br.com.dv.qrcodeapi.exception.InvalidMarginException;
import br.com.dv.qrcodeapi.security.JwtService;
import br.com.dv.qrcodeapi.security.TestSecurityConfig;
import br.com.dv.qrcodeapi.service.QRCodeGenerationService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QRCodeGenerationController.class)
@Import(TestSecurityConfig.class)
class QRCodeGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRCodeGenerationService qrCodeGenerationService;

    @MockBean
    private JwtService jwtService;

    private Cookie authCookie;

    @BeforeEach
    void setUp() {
        authCookie = new Cookie("token", "test.jwt.token");
        when(jwtService.extractEmail("test.jwt.token")).thenReturn("test@example.com");
        when(jwtService.validateToken("test.jwt.token", "test@example.com")).thenReturn(true);
    }

    private MockHttpServletRequestBuilder withCookie(MockHttpServletRequestBuilder builder) {
        return builder.cookie(authCookie);
    }

    @Test
    @DisplayName("Should successfully generate QR code when all parameters are valid")
    void shouldGenerateQRCodeWithValidParameters() throws Exception {
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("size", "250")
                        .param("correction", "L")
                        .param("type", "png")
                        .param("fcolor", "#000000")
                        .param("bcolor", "#FFFFFF")
                        .param("margin", "4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[]{1, 2, 3}));
    }

    @Test
    @DisplayName("Should successfully generate QR code when only required parameters are provided")
    void shouldGenerateQRCodeWithDefaultParameters() throws Exception {
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate")).param("contents", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when size parameter is not a valid number")
    void shouldReturnBadRequestForInvalidSize() throws Exception {
        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("size", "invalid")
                        .param("correction", "L")
                        .param("type", "png"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"X", "Y", "invalid", "A"})
    @DisplayName("Should return 400 Bad Request when correction level is not L, M, Q, or H")
    void shouldReturnBadRequestForInvalidCorrectionLevel(String correction) throws Exception {
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), eq(correction), anyString(), anyString(), anyString(), anyInt())
        ).thenThrow(new InvalidCorrectionLevelException());

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("size", "250")
                        .param("correction", correction)
                        .param("type", "png"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when content parameter is empty")
    void shouldReturnBadRequestForEmptyContent() throws Exception {
        when(qrCodeGenerationService.generateQRCode(
                eq(""), anyInt(), anyString(), anyString(), anyString(), anyString(), anyInt())
        ).thenThrow(new InvalidContentException());

        mockMvc.perform(withCookie(get("/api/qrcode/generate")).param("contents", ""))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "gif"})
    @DisplayName("Should successfully generate QR code with specified image format")
    void shouldGenerateQRCodeWithValidFormat(String format) throws Exception {
        MediaType expectedType = switch (format) {
            case "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.IMAGE_PNG;
        };
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, expectedType);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), eq(format), anyString(), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("type", format))
                .andExpect(status().isOk())
                .andExpect(content().contentType(expectedType));
    }

    @Test
    @DisplayName("Should successfully generate QR code when content contains special characters")
    void shouldGenerateQRCodeWithSpecialCharacters() throws Exception {
        String specialContent = "Hello! こんにちは! ❤️ #@$%";
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate")).param("contents", specialContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @DisplayName("Should successfully generate QR code when content exceeds typical length")
    void shouldGenerateQRCodeWithLongContent() throws Exception {
        String longContent = "a".repeat(1000);
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate")).param("contents", longContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @DisplayName("Should successfully generate QR code with valid named color")
    void shouldGenerateQRCodeWithValidNamedColor() throws Exception {
        var mockResponse = new ImageResponse(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), eq("RED"), anyString(), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("fcolor", "RED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when color name is invalid")
    void shouldReturnBadRequestForInvalidColor() throws Exception {
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), eq("RANDOM"), anyString(), anyInt())
        ).thenThrow(new InvalidColorException());

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("fcolor", "RANDOM"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully generate QR code with custom background color")
    void shouldGenerateQRCodeWithCustomBackgroundColor() throws Exception {
        var mockResponse = new ImageResponse(new byte[]{4, 5, 6}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), eq("#FFFF00"), anyInt())
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("bcolor", "#FFFF00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[]{4, 5, 6}));
    }

    @Test
    @DisplayName("Should successfully generate QR code with valid margin value")
    void shouldGenerateQRCodeWithValidMargin() throws Exception {
        var mockResponse = new ImageResponse(new byte[]{7, 8, 9}, MediaType.IMAGE_PNG);
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), eq(10))
        ).thenReturn(mockResponse);

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("margin", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[]{7, 8, 9}));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when margin value is out of allowed range")
    void shouldReturnBadRequestForInvalidMargin() throws Exception {
        when(qrCodeGenerationService.generateQRCode(
                anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), eq(999))
        ).thenThrow(new InvalidMarginException());

        mockMvc.perform(withCookie(get("/api/qrcode/generate"))
                        .param("contents", "test")
                        .param("margin", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when authentication cookie is missing")
    void shouldReturnUnauthorizedForMissingAuth() throws Exception {
        mockMvc.perform(get("/api/qrcode/generate")
                        .param("contents", "test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when JWT token is invalid")
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        Cookie invalidCookie = new Cookie("token", "invalid.jwt.token");
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(get("/api/qrcode/generate")
                        .cookie(invalidCookie)
                        .param("contents", "test"))
                .andExpect(status().isUnauthorized());
    }

}
