package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.LoginResponse;
import br.com.dv.qrcodeapi.dto.RegisterResponse;
import br.com.dv.qrcodeapi.dto.LoginRequest;
import br.com.dv.qrcodeapi.dto.RegisterRequest;
import br.com.dv.qrcodeapi.security.JwtService;
import br.com.dv.qrcodeapi.service.AppUserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String TOKEN_COOKIE = "token";

    private final AppUserServiceImpl appUserService;
    private final JwtService jwtService;

    public AuthController(AppUserServiceImpl appUserService, JwtService jwtService) {
        this.appUserService = appUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(appUserService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = appUserService.login(request);

        String token = jwtService.generateToken(request.email());
        
        Cookie cookie = new Cookie(TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
        
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout") 
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        
        return ResponseEntity.ok().build();
    }

}
