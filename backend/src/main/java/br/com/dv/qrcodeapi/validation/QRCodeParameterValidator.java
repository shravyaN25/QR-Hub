package br.com.dv.qrcodeapi.validation;

import br.com.dv.qrcodeapi.enums.Color;
import br.com.dv.qrcodeapi.enums.ImageFormat;
import br.com.dv.qrcodeapi.exception.*;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@Component
public class QRCodeParameterValidator {

    private static final int MIN_IMAGE_SIZE = 150;
    private static final int MAX_IMAGE_SIZE = 350;
    private static final int MIN_MARGIN = 0;
    private static final int MAX_MARGIN = 50;
    private static final Set<ImageFormat> SUPPORTED_IMAGE_FORMATS = EnumSet.allOf(ImageFormat.class);
    private static final Set<ErrorCorrectionLevel> CORRECTION_LEVELS = EnumSet.allOf(ErrorCorrectionLevel.class);

    public void validate(
            String content,
            int size,
            String correction,
            String format,
            String fcolor,
            String bcolor,
            int margin
    ) {
        validateContent(content);
        validateImageSize(size);
        validateCorrectionLevel(correction);
        validateImageFormat(format);
        validateColorValue(fcolor);
        validateColorValue(bcolor);
        validateMargin(margin);
    }

    private void validateContent(String content) {
        if (isContentInvalid(content)) {
            throw new InvalidContentException();
        }
    }

    private void validateImageSize(int size) {
        if (isSizeInvalid(size)) {
            throw new InvalidImageSizeException();
        }
    }

    private void validateCorrectionLevel(String correction) {
        if (isCorrectionLevelInvalid(correction)) {
            throw new InvalidCorrectionLevelException();
        }
    }

    private void validateImageFormat(String format) {
        if (isFormatInvalid(format)) {
            throw new InvalidImageFormatException();
        }
    }

    private void validateColorValue(String color) {
        if (isColorInvalid(color)) {
            throw new InvalidColorException();
        }
    }

    private void validateMargin(int margin) {
        if (isMarginInvalid(margin)) {
            throw new InvalidMarginException();
        }
    }

    private boolean isContentInvalid(String content) {
        return content == null || content.isBlank();
    }

    private boolean isSizeInvalid(int size) {
        return size < MIN_IMAGE_SIZE || size > MAX_IMAGE_SIZE;
    }

    private boolean isCorrectionLevelInvalid(String correction) {
        return correction == null ||
                CORRECTION_LEVELS.stream().noneMatch(c -> c.name().equalsIgnoreCase(correction));
    }

    private boolean isFormatInvalid(String format) {
        return format == null ||
                SUPPORTED_IMAGE_FORMATS.stream().noneMatch(f -> f.name().equalsIgnoreCase(format));
    }

    private boolean isColorInvalid(String color) {
        if (color == null) {
            return false;
        }
        return !isValidHexColor(color) && !isValidNamedColor(color);
    }

    private boolean isValidHexColor(String color) {
        return color.matches("^#([A-Fa-f0-9]{6})$");
    }

    private boolean isValidNamedColor(String color) {
        return Arrays.stream(Color.values())
                .anyMatch(c -> c.name().equalsIgnoreCase(color));
    }

    private boolean isMarginInvalid(int margin) {
        return margin < MIN_MARGIN || margin > MAX_MARGIN;
    }

}
