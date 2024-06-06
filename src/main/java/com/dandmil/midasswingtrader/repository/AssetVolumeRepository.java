package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.AssetVolume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetVolumeRepository extends JpaRepository<AssetVolume,UUID> {



}
