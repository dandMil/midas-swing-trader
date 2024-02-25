package com.dandmil.midasswingtrader.repository;


import com.dandmil.midasswingtrader.pojo.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, String> {

    List<Asset> findByName(String name);
    List<Asset> findAllByNameIn(List<String> names);

}