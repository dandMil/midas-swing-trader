package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.pojo.WatchlistEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface WatchListRepository extends JpaRepository<WatchlistEntry,String> {
    @Transactional
    void deleteByName(String name);
    }

