package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.RegisterResponse;
import br.com.dv.qrcodeapi.dto.RegisterRequest;
import br.com.dv.qrcodeapi.entity.AppUser;
import br.com.dv.qrcodeapi.exception.EmailAlreadyExistsException;
import br.com.dv.qrcodeapi.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        appUserService = new AppUserServiceImpl(appUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should successfully register new user when registration data is valid")
    void shouldRegisterNewUserWithValidData() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "password123", "Test User"
        );
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        RegisterResponse response = appUserService.register(request);

        assertEquals("User registered successfully", response.message());
        verify(appUserRepository).save(any(AppUser.class));
        verify(passwordEncoder).encode(request.password());
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when registering with existing email")
    void shouldThrowExceptionForExistingEmail() {
        RegisterRequest request = new RegisterRequest(
                "existing@example.com", "password123", "Test User"
        );

        when(appUserRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> appUserService.register(request));
        verify(appUserRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

}
