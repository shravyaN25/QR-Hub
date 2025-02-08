package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;
import br.com.dv.qrcodeapi.entity.QRCode;
import br.com.dv.qrcodeapi.exception.QRCodeNotFoundException;
import br.com.dv.qrcodeapi.security.JwtService;
import br.com.dv.qrcodeapi.security.TestSecurityConfig;
import br.com.dv.qrcodeapi.service.QRCodeManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QRCodeManagementController.class)
@Import(TestSecurityConfig.class)
class QRCodeManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRCodeManagementService qrCodeManagementService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie authCookie;

    @BeforeEach
    void setUp() {
        authCookie = new Cookie("token", "test.jwt.token");
        when(jwtService.extractEmail("test.jwt.token")).thenReturn("test@example.com");
        when(jwtService.validateToken("test.jwt.token", "test@example.com")).thenReturn(true);
    }

    @Test
    @DisplayName("Should return 200 and successfully create QR code when request data is valid")
    void shouldCreateQRCodeWithValidData() throws Exception {
        SaveQRCodeRequest request = createValidRequest();
        QRCodeResponse response = createValidResponse();

        when(qrCodeManagementService.save(any(SaveQRCodeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/qrcode")
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value("https://example.com"))
                .andExpect(jsonPath("$.name").value("Test QR"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.size").value(250))
                .andExpect(jsonPath("$.format").value("png"))
                .andExpect(jsonPath("$.foregroundColor").value("#000000"))
                .andExpect(jsonPath("$.backgroundColor").value("#FFFFFF"))
                .andExpect(jsonPath("$.margin").value(4))
                .andExpect(jsonPath("$.errorCorrection").value("M"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 200 and successfully retrieve all QR codes for authenticated user")
    void shouldRetrieveAllQRCodes() throws Exception {
        QRCodeResponse response1 = createQRCodeResponse(createValidQRCode());
        QRCodeResponse response2 = createQRCodeResponse(createValidQRCode());

        when(qrCodeManagementService.findAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/qrcode")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(response1.id().toString()))
                .andExpect(jsonPath("$[0].content").value("https://example.com"))
                .andExpect(jsonPath("$[0].name").value("Test QR"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].size").value(250))
                .andExpect(jsonPath("$[0].format").value("png"))
                .andExpect(jsonPath("$[0].foregroundColor").value("#000000"))
                .andExpect(jsonPath("$[0].backgroundColor").value("#FFFFFF"))
                .andExpect(jsonPath("$[0].margin").value(4))
                .andExpect(jsonPath("$[0].errorCorrection").value("M"))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(response2.id().toString()))
                .andExpect(jsonPath("$[1].content").value("https://example.com"))
                .andExpect(jsonPath("$[1].name").value("Test QR"))
                .andExpect(jsonPath("$[1].description").value("Test Description"))
                .andExpect(jsonPath("$[1].size").value(250))
                .andExpect(jsonPath("$[1].format").value("png"))
                .andExpect(jsonPath("$[1].foregroundColor").value("#000000"))
                .andExpect(jsonPath("$[1].backgroundColor").value("#FFFFFF"))
                .andExpect(jsonPath("$[1].margin").value(4))
                .andExpect(jsonPath("$[1].errorCorrection").value("M"))
                .andExpect(jsonPath("$[1].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[1].updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 200 and successfully retrieve QR code when searching by valid ID")
    void shouldRetrieveQRCodeById() throws Exception {
        QRCode qrCode = createValidQRCode();
        UUID id = qrCode.getId();
        QRCodeResponse expectedResponse = createQRCodeResponse(qrCode);

        when(qrCodeManagementService.findById(id)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/qrcode/" + id)
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.content").value("https://example.com"))
                .andExpect(jsonPath("$.name").value("Test QR"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.size").value(250))
                .andExpect(jsonPath("$.format").value("png"))
                .andExpect(jsonPath("$.foregroundColor").value("#000000"))
                .andExpect(jsonPath("$.backgroundColor").value("#FFFFFF"))
                .andExpect(jsonPath("$.margin").value(4))
                .andExpect(jsonPath("$.errorCorrection").value("M"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when QR code ID does not exist")
    void shouldReturnNotFoundForNonexistentQRCode() throws Exception {
        UUID id = UUID.randomUUID();

        when(qrCodeManagementService.findById(id))
                .thenThrow(new QRCodeNotFoundException(id));

        mockMvc.perform(get("/api/qrcode/" + id)
                        .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        String.format("QR code with ID '%s' not found", id)
                ));

        verify(qrCodeManagementService).findById(id);
    }

    @Test
    @DisplayName("Should return 200 and successfully update QR code when request data is valid")
    void shouldUpdateQRCodeWithValidData() throws Exception {
        UUID id = UUID.randomUUID();
        SaveQRCodeRequest request = createValidRequest();
        QRCodeResponse response = createValidResponse();

        when(qrCodeManagementService.update(eq(id), any(SaveQRCodeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/qrcode/" + id)
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.content").value("https://example.com"))
                .andExpect(jsonPath("$.name").value("Test QR"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.size").value(250))
                .andExpect(jsonPath("$.format").value("png"))
                .andExpect(jsonPath("$.foregroundColor").value("#000000"))
                .andExpect(jsonPath("$.backgroundColor").value("#FFFFFF"))
                .andExpect(jsonPath("$.margin").value(4))
                .andExpect(jsonPath("$.errorCorrection").value("M"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 200 and successfully delete QR code when it exists")
    void shouldDeleteExistingQRCode() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(qrCodeManagementService).delete(id);

        mockMvc.perform(delete("/api/qrcode/" + id)
                        .cookie(authCookie))
                .andExpect(status().isNoContent());

        verify(qrCodeManagementService).delete(id);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existent QR code")
    void shouldReturnNotFoundForDeletingNonexistentQRCode() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new QRCodeNotFoundException(id))
                .when(qrCodeManagementService)
                .delete(id);

        mockMvc.perform(delete("/api/qrcode/" + id)
                        .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        String.format("QR code with ID '%s' not found", id)
                ));

        verify(qrCodeManagementService).delete(id);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request validation fails")
    void shouldReturnBadRequestForInvalidData() throws Exception {
        SaveQRCodeRequest request = new SaveQRCodeRequest(
                "",
                "",
                "Test Description",
                250,
                "png",
                "#000000",
                "#FFFFFF",
                4,
                "M"
        );

        mockMvc.perform(post("/api/qrcode")
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(qrCodeManagementService, never()).save(any());
    }

    private SaveQRCodeRequest createValidRequest() {
        return new SaveQRCodeRequest(
                "https://example.com",
                "Test QR",
                "Test Description",
                250,
                "png",
                "#000000",
                "#FFFFFF",
                4,
                "M"
        );
    }

    private QRCodeResponse createValidResponse() {
        return new QRCodeResponse(
                UUID.randomUUID(),
                "https://example.com",
                "Test QR",
                "Test Description",
                250,
                "png",
                "#000000",
                "#FFFFFF",
                4,
                "M",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private QRCode createValidQRCode() {
        QRCode qrCode = new QRCode();
        qrCode.setId(UUID.randomUUID());
        qrCode.setContent("https://example.com");
        qrCode.setName("Test QR");
        qrCode.setDescription("Test Description");
        qrCode.setSize(250);
        qrCode.setFormat("png");
        qrCode.setForegroundColor("#000000");
        qrCode.setBackgroundColor("#FFFFFF");
        qrCode.setMargin(4);
        qrCode.setErrorCorrection("M");
        qrCode.setCreatedAt(LocalDateTime.now());
        qrCode.setUpdatedAt(LocalDateTime.now());
        return qrCode;
    }

    private QRCodeResponse createQRCodeResponse(QRCode qrCode) {
        return new QRCodeResponse(
                qrCode.getId(),
                qrCode.getContent(),
                qrCode.getName(),
                qrCode.getDescription(),
                qrCode.getSize(),
                qrCode.getFormat(),
                qrCode.getForegroundColor(),
                qrCode.getBackgroundColor(),
                qrCode.getMargin(),
                qrCode.getErrorCorrection(),
                qrCode.getCreatedAt(),
                qrCode.getUpdatedAt()
        );
    }

}
