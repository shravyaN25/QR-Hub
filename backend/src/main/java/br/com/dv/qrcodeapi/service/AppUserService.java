package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.LoginResponse;
import br.com.dv.qrcodeapi.dto.RegisterResponse;
import br.com.dv.qrcodeapi.dto.LoginRequest;
import br.com.dv.qrcodeapi.dto.RegisterRequest;

public interface AppUserService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

}
