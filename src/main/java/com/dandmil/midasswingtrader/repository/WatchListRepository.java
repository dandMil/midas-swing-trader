package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.WatchlistEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<WatchlistEntry,String> {
    @Transactional
    void deleteByName(String name);
    }

