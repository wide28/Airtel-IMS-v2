package com.airtel.ims.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_tag", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Asset tag is required")
    private String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    @NotNull(message = "Device type is required")
    private DeviceType deviceType;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Brand is required")
    private String brand;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Model is required")
    private String model;

    @Column(name = "serial_number", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @Column(length = 100)
    private String processor;

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(name = "storage_gb")
    private Integer storageGb;

    @Column(length = 100)
    private String os;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status")
    @Builder.Default
    private ConditionStatus conditionStatus = ConditionStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssetStatus status = AssetStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
    private List<Assignment> assignments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DeviceType {
        LAPTOP, DESKTOP, MOBILE_PHONE, TABLET, OTHER
    }

    public enum ConditionStatus {
        NEW, GOOD, FAIR, POOR, DAMAGED, DECOMMISSIONED
    }

    public enum AssetStatus {
        AVAILABLE, ASSIGNED, IN_REPAIR, RETIRED
    }
}
