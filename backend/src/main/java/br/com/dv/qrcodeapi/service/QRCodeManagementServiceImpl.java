package br.com.dv.qrcodeapi.service;

import br.com.dv.qrcodeapi.dto.QRCodeResponse;
import br.com.dv.qrcodeapi.dto.SaveQRCodeRequest;
import br.com.dv.qrcodeapi.entity.AppUser;
import br.com.dv.qrcodeapi.entity.QRCode;
import br.com.dv.qrcodeapi.exception.QRCodeNotFoundException;
import br.com.dv.qrcodeapi.mapper.QRCodeMapper;
import br.com.dv.qrcodeapi.repository.AppUserRepository;
import br.com.dv.qrcodeapi.repository.QRCodeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QRCodeManagementServiceImpl implements QRCodeManagementService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final QRCodeRepository qrCodeRepository;
    private final AppUserRepository appUserRepository;
    private final QRCodeMapper qrCodeMapper;

    public QRCodeManagementServiceImpl(
            QRCodeRepository qrCodeRepository,
            AppUserRepository appUserRepository,
            QRCodeMapper qrCodeMapper
    ) {
        this.qrCodeRepository = qrCodeRepository;
        this.appUserRepository = appUserRepository;
        this.qrCodeMapper = qrCodeMapper;
    }

    @Override
    @Transactional
    public QRCodeResponse save(SaveQRCodeRequest request) {
        AppUser currentUser = getCurrentUser();

        QRCode qrCode = qrCodeMapper.toEntity(request);
        qrCode.setOwner(currentUser);

        QRCode saved = qrCodeRepository.saveAndFlush(qrCode);
        return qrCodeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QRCodeResponse> findAll() {
        AppUser currentUser = getCurrentUser();
        return qrCodeRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(qrCodeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QRCodeResponse findById(UUID id) {
        AppUser currentUser = getCurrentUser();
        QRCode qrCode = qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new QRCodeNotFoundException(id));
        return qrCodeMapper.toResponse(qrCode);
    }

    @Override
    @Transactional
    public QRCodeResponse update(UUID id, SaveQRCodeRequest request) {
        AppUser currentUser = getCurrentUser();
        QRCode qrCode = qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new QRCodeNotFoundException(id));

        qrCodeMapper.update(qrCode, request);

        QRCode updated = qrCodeRepository.save(qrCode);
        return qrCodeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AppUser currentUser = getCurrentUser();
        QRCode qrCode = qrCodeRepository.findByOwnerIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new QRCodeNotFoundException(id));

        qrCode.setDeleted(true);
        qrCode.setDeletedAt(LocalDateTime.now());

        qrCodeRepository.save(qrCode);
    }

    private AppUser getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return appUserRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));
    }

}
