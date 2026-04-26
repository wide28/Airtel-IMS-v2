package com.airtel.ims.service;

import com.airtel.ims.model.*;
import com.airtel.ims.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AuditLogRepository auditLogRepository;

    public Page<Asset> searchAssets(Asset.AssetStatus status, Asset.DeviceType deviceType,
                                    Long departmentId, String search, Pageable pageable) {
        return assetRepository.searchAssets(status, deviceType, departmentId, search, pageable);
    }

    public Optional<Asset> findById(Long id) {
        return assetRepository.findById(id);
    }

    public Asset save(Asset asset) {
        boolean isNew = asset.getId() == null;
        Asset saved = assetRepository.save(asset);
        auditLogRepository.save(AuditLog.builder()
                .entityType("ASSET")
                .entityId(saved.getId())
                .action(isNew ? "CREATED" : "UPDATED")
                .description((isNew ? "Asset registered: " : "Asset updated: ") + saved.getAssetTag())
                .performedBy("ADMIN")
                .build());
        return saved;
    }

    public void delete(Long id) {
        assetRepository.findById(id).ifPresent(asset -> {
            auditLogRepository.save(AuditLog.builder()
                    .entityType("ASSET")
                    .entityId(id)
                    .action("DELETED")
                    .description("Asset deleted: " + asset.getAssetTag())
                    .performedBy("ADMIN")
                    .build());
            assetRepository.deleteById(id);
        });
    }

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("totalAssets", assetRepository.count());
        stats.put("available", assetRepository.countByStatus(Asset.AssetStatus.AVAILABLE));
        stats.put("assigned", assetRepository.countByStatus(Asset.AssetStatus.ASSIGNED));
        stats.put("inRepair", assetRepository.countByStatus(Asset.AssetStatus.IN_REPAIR));
        stats.put("retired", assetRepository.countByStatus(Asset.AssetStatus.RETIRED));
        stats.put("laptops", assetRepository.countByDeviceType(Asset.DeviceType.LAPTOP));
        stats.put("desktops", assetRepository.countByDeviceType(Asset.DeviceType.DESKTOP));
        stats.put("mobilePhones", assetRepository.countByDeviceType(Asset.DeviceType.MOBILE_PHONE));
        return stats;
    }

    public boolean assetTagExists(String tag) { return assetRepository.existsByAssetTag(tag); }
    public boolean serialNumberExists(String sn) { return assetRepository.existsBySerialNumber(sn); }

    public List<Asset> findAvailableAssets() {
        return assetRepository.findByStatus(Asset.AssetStatus.AVAILABLE);
    }

    public List<AuditLog> getAssetHistory(Long assetId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByPerformedAtDesc("ASSET", assetId);
    }
}