package br.com.dv.qrcodeapi.repository;

import br.com.dv.qrcodeapi.entity.AppUser;
import br.com.dv.qrcodeapi.entity.QRCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QRCodeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");
        entityManager.persist(user);
        entityManager.flush();
    }

    private QRCode createQRCode(String name) {
        QRCode qrCode = new QRCode();
        qrCode.setContent("https://example.com");
        qrCode.setName(name);
        qrCode.setDescription("Test Description");
        qrCode.setSize(250);
        qrCode.setFormat("png");
        qrCode.setForegroundColor("#000000");
        qrCode.setBackgroundColor("#FFFFFF");
        qrCode.setMargin(4);
        qrCode.setErrorCorrection("M");
        qrCode.setOwner(user);
        return qrCode;
    }

    @Test
    @DisplayName("Should successfully find all QR codes associated with owner ID")
    void shouldFindQRCodesByOwnerId() {
        QRCode qrCode1 = createQRCode("First QR");
        QRCode qrCode2 = createQRCode("Second QR");

        entityManager.persist(qrCode1);
        entityManager.persist(qrCode2);
        entityManager.flush();

        List<QRCode> found = qrCodeRepository.findByOwnerId(user.getId());

        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(qr -> qr.getName().equals("First QR")));
        assertTrue(found.stream().anyMatch(qr -> qr.getName().equals("Second QR")));
    }

    @Test
    @DisplayName("Should successfully find QR code by owner ID and QR code ID combination")
    void shouldFindQRCodeByOwnerIdAndQRCodeId() {
        QRCode qrCode = createQRCode("Test QR");
        entityManager.persist(qrCode);
        entityManager.flush();

        Optional<QRCode> found = qrCodeRepository
                .findByOwnerIdAndId(user.getId(), qrCode.getId());

        assertTrue(found.isPresent());
        assertEquals("Test QR", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when searching with incorrect owner ID")
    void shouldReturnEmptyForIncorrectOwnerId() {
        QRCode qrCode = createQRCode("Test QR");
        entityManager.persist(qrCode);
        entityManager.flush();

        Optional<QRCode> found = qrCodeRepository
                .findByOwnerIdAndId(UUID.randomUUID(), qrCode.getId());

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should not return soft-deleted QR codes in find all query")
    void shouldNotReturnDeletedQRCodes() {
        QRCode qrCode = createQRCode("Test QR");
        qrCode.setDeleted(true);
        qrCode.setDeletedAt(LocalDateTime.now());
        entityManager.persist(qrCode);
        entityManager.flush();

        List<QRCode> found = qrCodeRepository.findByOwnerId(user.getId());

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should not return soft-deleted QR code in find by ID query")
    void shouldNotReturnDeletedQRCodeById() {
        QRCode qrCode = createQRCode("Test QR");
        qrCode.setDeleted(true);
        qrCode.setDeletedAt(LocalDateTime.now());
        entityManager.persist(qrCode);
        entityManager.flush();

        Optional<QRCode> found = qrCodeRepository
                .findByOwnerIdAndId(user.getId(), qrCode.getId());

        assertTrue(found.isEmpty());
    }

}
