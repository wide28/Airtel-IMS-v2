package com.airtel.ims.repository;

import com.airtel.ims.model.Asset;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("SELECT a FROM Asset a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:deviceType IS NULL OR a.deviceType = :deviceType) AND " +
           "(:departmentId IS NULL OR a.department.id = :departmentId) AND " +
           "(:search IS NULL OR LOWER(a.assetTag) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "  OR LOWER(a.brand) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "  OR LOWER(a.model) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "  OR LOWER(a.serialNumber) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<Asset> searchAssets(
            @Param("status") Asset.AssetStatus status,
            @Param("deviceType") Asset.DeviceType deviceType,
            @Param("departmentId") Long departmentId,
            @Param("search") String search,
            Pageable pageable);

    long countByStatus(Asset.AssetStatus status);
    long countByDeviceType(Asset.DeviceType deviceType);
    List<Asset> findByStatus(Asset.AssetStatus status);
    boolean existsByAssetTag(String assetTag);
    boolean existsBySerialNumber(String serialNumber);
}
