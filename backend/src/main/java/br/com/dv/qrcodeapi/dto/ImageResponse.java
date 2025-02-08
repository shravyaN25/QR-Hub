package br.com.dv.qrcodeapi.dto;

import org.springframework.http.MediaType;
import java.util.Arrays;
import java.util.Objects;

public record ImageResponse(byte[] imageData, MediaType mediaType) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageResponse that = (ImageResponse) o;
        return Arrays.equals(imageData, that.imageData) && Objects.equals(mediaType, that.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(imageData), mediaType);
    }

    @Override
    public String toString() {
        return "ImageResponse[imageData=" + Arrays.toString(imageData) + ", mediaType=" + mediaType + "]";
    }

}
