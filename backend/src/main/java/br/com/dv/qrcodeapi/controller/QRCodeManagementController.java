package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;
import br.com.dv.qrcodeapi.service.QRCodeManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeManagementController {

    private final QRCodeManagementService qrCodeManagementService;

    public QRCodeManagementController(QRCodeManagementService qrCodeManagementService) {
        this.qrCodeManagementService = qrCodeManagementService;
    }

    @PostMapping
    public ResponseEntity<QRCodeResponse> save(@Valid @RequestBody SaveQRCodeRequest request) {
        return ResponseEntity.ok(qrCodeManagementService.save(request));
    }

    @GetMapping
    public ResponseEntity<List<QRCodeResponse>> findAll() {
        return ResponseEntity.ok(qrCodeManagementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QRCodeResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(qrCodeManagementService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QRCodeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SaveQRCodeRequest request
    ) {
        return ResponseEntity.ok(qrCodeManagementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        qrCodeManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
