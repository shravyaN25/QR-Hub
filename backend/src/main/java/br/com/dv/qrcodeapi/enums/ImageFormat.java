package br.com.dv.qrcodeapi.enums;

import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public enum ImageFormat {

    PNG(MediaType.IMAGE_PNG),
    JPEG(MediaType.IMAGE_JPEG),
    GIF(MediaType.IMAGE_GIF);

    private final MediaType mediaType;

    ImageFormat(MediaType mediaType) {
        this.mediaType = mediaType;
    }

}
