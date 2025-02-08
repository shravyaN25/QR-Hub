package br.com.dv.qrcodeapi.repository;

import br.com.dv.qrcodeapi.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QRCodeRepository extends JpaRepository<QRCode, UUID> {

    @Query("SELECT q FROM QRCode q WHERE q.owner.id = :ownerId AND q.deleted = false")
    List<QRCode> findByOwnerId(UUID ownerId);

    @Query("SELECT q FROM QRCode q WHERE q.owner.id = :ownerId AND q.id = :id AND q.deleted = false")
    Optional<QRCode> findByOwnerIdAndId(UUID ownerId, UUID id);

}
