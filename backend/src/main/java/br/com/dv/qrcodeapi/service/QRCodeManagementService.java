package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;

import java.util.List;
import java.util.UUID;

public interface QRCodeManagementService {

    QRCodeResponse save(SaveQRCodeRequest request);

    List<QRCodeResponse> findAll();

    QRCodeResponse findById(UUID id);

    QRCodeResponse update(UUID id, SaveQRCodeRequest request);

    void delete(UUID id);

}
