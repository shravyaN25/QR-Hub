package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.LoginResponse;
import br.com.dv.qrcodeapi.dto.RegisterResponse;
import br.com.dv.qrcodeapi.dto.LoginRequest;
import br.com.dv.qrcodeapi.dto.RegisterRequest;
import br.com.dv.qrcodeapi.entity.AppUser;
import br.com.dv.qrcodeapi.exception.EmailAlreadyExistsException;
import br.com.dv.qrcodeapi.exception.InvalidCredentialsException;
import br.com.dv.qrcodeapi.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserServiceImpl implements AppUserService {

    private static final String USER_REGISTERED_MESSAGE = "User registered successfully";
    private static final String USER_LOGGED_IN_MESSAGE = "User logged in successfully";

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());

        appUserRepository.save(user);

        return new RegisterResponse(USER_REGISTERED_MESSAGE);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponse(USER_LOGGED_IN_MESSAGE);
    }

}
