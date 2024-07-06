package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.PortfolioEntity;
import com.dandmil.midasswingtrader.pojo.PortfolioEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity,String> {


    List<PortfolioEntity> findAllByName(String name);
    PortfolioEntity findByName(String name);
//    void deleteAllByName(String name);
    @Transactional
    void deleteByName(String name);
    boolean existsByName(String name);
}
