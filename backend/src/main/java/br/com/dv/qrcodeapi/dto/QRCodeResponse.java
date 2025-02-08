package br.com.dv.qrcodeapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record QRCodeResponse(
        UUID id,
        String content,
        String name,
        String description,
        Integer size,
        String format,
        String foregroundColor,
        String backgroundColor,
        Integer margin,
        String errorCorrection,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
