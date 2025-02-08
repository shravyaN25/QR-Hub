package br.com.dv.qrcodeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "qr_code")
@Getter
@Setter
public class QRCode {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String foregroundColor;

    @Column(nullable = false)
    private String backgroundColor;

    @Column(nullable = false)
    private Integer margin;

    @Column(nullable = false)
    private String errorCorrection;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

}
