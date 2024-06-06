package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.AssetSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface AssetSignalRepository extends JpaRepository<AssetSignal,UUID> {


}
