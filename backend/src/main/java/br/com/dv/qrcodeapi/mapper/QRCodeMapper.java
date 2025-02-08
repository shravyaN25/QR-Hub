package br.com.dv.qrcodeapi.mapper;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;
import br.com.dv.qrcodeapi.entity.QRCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface QRCodeMapper {

    QRCodeResponse toResponse(QRCode qrCode);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    QRCode toEntity(SaveQRCodeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void update(@MappingTarget QRCode qrCode, SaveQRCodeRequest request);

}
