package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.pojo.VolumeWatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolumeWatchlistRepository extends JpaRepository<VolumeWatchlistEntry,String> {

    List<VolumeWatchlistEntry> findAllByNameInOrderByDateCreatedDesc(List<String> names);

    List<VolumeWatchlistEntry> findAllByNameOrderByDateCreatedDesc(String name);

    boolean existsByName(String name);


}
