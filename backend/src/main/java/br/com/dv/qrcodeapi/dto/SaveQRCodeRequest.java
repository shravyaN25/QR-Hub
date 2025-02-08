package br.com.dv.qrcodeapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveQRCodeRequest(
        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String name,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description,

        Integer size,
        String format,
        String foregroundColor,
        String backgroundColor,
        Integer margin,
        String errorCorrection
) {}
