package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;
import br.com.dv.qrcodeapi.entity.AppUser;
import br.com.dv.qrcodeapi.entity.QRCode;
import br.com.dv.qrcodeapi.exception.QRCodeNotFoundException;
import br.com.dv.qrcodeapi.mapper.QRCodeMapper;
import br.com.dv.qrcodeapi.repository.AppUserRepository;
import br.com.dv.qrcodeapi.repository.QRCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QRCodeManagementServiceTest {

    @Mock
    private QRCodeRepository qrCodeRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private QRCodeMapper qrCodeMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private QRCodeManagementService qrCodeManagementService;

    private AppUser currentUser;

    @BeforeEach
    void setUp() {
        qrCodeManagementService = new QRCodeManagementServiceImpl(
                qrCodeRepository,
                appUserRepository,
                qrCodeMapper
        );
        currentUser = new AppUser();
        currentUser.setId(UUID.randomUUID());
        currentUser.setEmail("test@example.com");

        User userDetails = new User(
                currentUser.getEmail(),
                "password",
                List.of()
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(appUserRepository.findByEmail(currentUser.getEmail()))
                .thenReturn(Optional.of(currentUser));
    }

    @Test
    @DisplayName("Should successfully save QR code when all parameters are valid")
    void shouldSaveQRCodeWithValidParameters() {
        SaveQRCodeRequest request = createValidRequest();
        QRCode qrCode = createValidQRCode();
        QRCodeResponse expectedResponse = createQRCodeResponse(qrCode);

        when(qrCodeMapper.toEntity(request)).thenReturn(qrCode);
        when(qrCodeRepository.saveAndFlush(any(QRCode.class))).thenReturn(qrCode);
        when(qrCodeMapper.toResponse(qrCode)).thenReturn(expectedResponse);

        QRCodeResponse response = qrCodeManagementService.save(request);

        assertNotNull(response);
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.content(), response.content());
        verify(qrCodeRepository).saveAndFlush(any(QRCode.class));
    }

    @Test
    @DisplayName("Should successfully retrieve all QR codes for current user")
    void shouldRetrieveAllQRCodesForUser() {
        List<QRCode> qrCodes = List.of(createValidQRCode(), createValidQRCode());
        QRCodeResponse responseDto = createQRCodeResponse(qrCodes.get(0));

        when(qrCodeRepository.findByOwnerId(currentUser.getId())).thenReturn(qrCodes);
        when(qrCodeMapper.toResponse(any(QRCode.class))).thenReturn(responseDto);

        List<QRCodeResponse> responses = qrCodeManagementService.findAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(qrCodeRepository).findByOwnerId(currentUser.getId());
    }

    @Test
    @DisplayName("Should successfully retrieve QR code when searching by valid ID")
    void shouldRetrieveQRCodeById() {
        QRCode qrCode = createValidQRCode();
        UUID id = qrCode.getId();
        QRCodeResponse expectedResponse = createQRCodeResponse(qrCode);

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.of(qrCode));
        when(qrCodeMapper.toResponse(qrCode)).thenReturn(expectedResponse);

        QRCodeResponse response = qrCodeManagementService.findById(id);

        assertNotNull(response);
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.content(), response.content());
        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
    }

    @Test
    @DisplayName("Should throw QRCodeNotFoundException when QR code ID does not exist")
    void shouldThrowExceptionForNonexistentQRCode() {
        UUID id = UUID.randomUUID();

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.empty());

        assertThrows(QRCodeNotFoundException.class, () -> qrCodeManagementService.findById(id));
        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
    }

    @Test
    @DisplayName("Should successfully update QR code when it exists")
    void shouldUpdateExistingQRCode() {
        QRCode existingQRCode = createValidQRCode();
        UUID id = existingQRCode.getId();
        SaveQRCodeRequest updateRequest = createValidRequest();
        QRCodeResponse expectedResponse = createQRCodeResponse(existingQRCode);

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.of(existingQRCode));
        when(qrCodeRepository.save(any(QRCode.class))).thenReturn(existingQRCode);
        when(qrCodeMapper.toResponse(existingQRCode)).thenReturn(expectedResponse);
        doNothing().when(qrCodeMapper).update(existingQRCode, updateRequest);

        QRCodeResponse response = qrCodeManagementService.update(id, updateRequest);

        assertNotNull(response);
        assertEquals(expectedResponse.id(), response.id());
        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
        verify(qrCodeRepository).save(existingQRCode);
        verify(qrCodeMapper).update(existingQRCode, updateRequest);
    }

    @Test
    @DisplayName("Should throw QRCodeNotFoundException when updating non-existent QR code")
    void shouldThrowExceptionForUpdatingNonexistentQRCode() {
        UUID id = UUID.randomUUID();
        SaveQRCodeRequest updateRequest = createValidRequest();

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.empty());

        assertThrows(QRCodeNotFoundException.class,
                () -> qrCodeManagementService.update(id, updateRequest));
        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
        verify(qrCodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully perform soft delete when QR code exists")
    void shouldSoftDeleteExistingQRCode() {
        QRCode existingQRCode = createValidQRCode();
        UUID id = existingQRCode.getId();

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.of(existingQRCode));

        qrCodeManagementService.delete(id);

        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
        verify(qrCodeRepository).save(existingQRCode);
        assertTrue(existingQRCode.isDeleted());
        assertNotNull(existingQRCode.getDeletedAt());
    }

    @Test
    @DisplayName("Should throw QRCodeNotFoundException when deleting non-existent QR code")
    void shouldThrowExceptionForDeletingNonexistentQRCode() {
        UUID id = UUID.randomUUID();

        when(qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id))
                .thenReturn(Optional.empty());

        assertThrows(QRCodeNotFoundException.class, () -> qrCodeManagementService.delete(id));
        verify(qrCodeRepository).findByOwnerIdAndId(currentUser.getId(), id);
        verify(qrCodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not found in the system")
    void shouldThrowExceptionForNonexistentUser() {
        when(appUserRepository.findByEmail(currentUser.getEmail()))
                .thenReturn(Optional.empty());

        SaveQRCodeRequest request = createValidRequest();

        assertThrows(RuntimeException.class, () -> qrCodeManagementService.save(request));
        verify(appUserRepository).findByEmail(currentUser.getEmail());
        verify(qrCodeRepository, never()).save(any());
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
        qrCode.setOwner(currentUser);
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
