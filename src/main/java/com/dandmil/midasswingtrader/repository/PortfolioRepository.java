package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<PortfolioEntity,String> {


    List<PortfolioEntity> findAllByName(String name);
}
