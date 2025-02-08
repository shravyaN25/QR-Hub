package br.com.dv.qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/health")
@RestController
public class HealthController {

    @GetMapping
    public ResponseEntity<Void> ping() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
