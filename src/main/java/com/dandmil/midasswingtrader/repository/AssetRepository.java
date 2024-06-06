package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.entity.AssetSignal;
import com.dandmil.midasswingtrader.entity.AssetVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    @Query("SELECT a FROM Asset a")
    List<Asset> findAllAssets();

    @Query("SELECT av FROM AssetVolume av WHERE av.asset.assetId IN :assetIds")
    List<AssetVolume> findAllAssetVolumeByAssetIds(@Param("assetIds") List<UUID> assetIds);

    @Query("SELECT as FROM AssetSignal as WHERE as.asset.assetId IN :assetIds")
    List<AssetSignal> findAllAssetSignalByAssetIds(List<UUID> assetIds);

}
