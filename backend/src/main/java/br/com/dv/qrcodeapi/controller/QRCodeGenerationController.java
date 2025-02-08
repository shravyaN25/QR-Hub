package br.com.dv.qrcodeapi.controller;

import br.com.dv.qrcodeapi.dto.ImageResponse;
import br.com.dv.qrcodeapi.service.QRCodeGenerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequestMapping("/api/qrcode/generate")
@RestController
public class QRCodeGenerationController {

    private final QRCodeGenerationService qrCodeGenerationService;

    public QRCodeGenerationController(QRCodeGenerationService qrCodeGenerationService) {
        this.qrCodeGenerationService = qrCodeGenerationService;
    }

    @GetMapping
    public ResponseEntity<byte[]> generateQrCode(
            @RequestParam(name = "contents") String content,
            @RequestParam(required = false, defaultValue = "250") int size,
            @RequestParam(required = false, defaultValue = "L") String correction,
            @RequestParam(name = "type", required = false, defaultValue = "png") String format,
            @RequestParam(required = false, defaultValue = "#000000") String fcolor,
            @RequestParam(required = false, defaultValue = "#FFFFFF") String bcolor,
            @RequestParam(required = false, defaultValue = "4") int margin
    ) {
        ImageResponse response = qrCodeGenerationService.generateQRCode(
                content,
                size,
                correction,
                format,
                URLDecoder.decode(fcolor, StandardCharsets.UTF_8),
                URLDecoder.decode(bcolor, StandardCharsets.UTF_8),
                margin
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(response.mediaType())
                .body(response.imageData());
    }

}
