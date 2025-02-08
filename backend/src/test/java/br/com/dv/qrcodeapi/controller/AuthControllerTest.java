package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.LoginResponse;
import br.com.dv.qrcodeapi.security.TestSecurityConfig;
import br.com.dv.qrcodeapi.dto.RegisterResponse;
import br.com.dv.qrcodeapi.dto.LoginRequest;
import br.com.dv.qrcodeapi.dto.RegisterRequest;
import br.com.dv.qrcodeapi.exception.EmailAlreadyExistsException;
import br.com.dv.qrcodeapi.exception.InvalidCredentialsException;
import br.com.dv.qrcodeapi.service.AppUserServiceImpl;
import br.com.dv.qrcodeapi.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserServiceImpl appUserService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 200 OK and successfully register user when registration data is valid")
    void shouldRegisterWithValidData() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "Test User"
        );
        RegisterResponse expectedResponse = new RegisterResponse("User registered successfully");

        when(appUserService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when registration email format is invalid")
    void shouldReturnBadRequestForInvalidRegistrationEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "invalid-email",
                "validPassword123",
                "Test User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    @Test
    @DisplayName("Should return 409 Conflict when email is already registered")
    void shouldReturnConflictForExistingEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "password123",
                "Test User"
        );

        when(appUserService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(request.email()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(
                        String.format("Email '%s' already exists", request.email())
                ));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when registration password is too short")
    void shouldReturnBadRequestForShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "123",
                "Test User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Password must be at least 6 characters long"
                ));
    }

    @Test
    @DisplayName("Should return 200 OK and set cookie when login is successful")
    void shouldLogInWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse response = new LoginResponse("User logged in successfully");
        String mockToken = "test.jwt.token";

        when(appUserService.login(any(LoginRequest.class))).thenReturn(response);
        when(jwtService.generateToken(request.email())).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully"))
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().httpOnly("token", true));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login email is empty")
    void shouldReturnBadRequestForEmptyLoginEmail() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is required"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login email format is invalid")
    void shouldReturnBadRequestForInvalidLoginEmail() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login password is empty")
    void shouldReturnBadRequestForEmptyLoginPassword() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password is required"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when account does not exist")
    void shouldReturnUnauthorizedForNonexistentAccount() throws Exception {
        LoginRequest request = new LoginRequest("notfound@example.com", "password123");

        when(appUserService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when password is incorrect")
    void shouldReturnUnauthorizedForIncorrectPassword() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "wrong");

        when(appUserService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should return 200 OK and clear cookie when logout")
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().maxAge("token", 0));
    }

}
