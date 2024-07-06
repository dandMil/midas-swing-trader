package com.dandmil.midasswingtrader.repository;

import com.dandmil.midasswingtrader.entity.TradeRecommendationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRecommendationRepository extends JpaRepository<TradeRecommendationEntity,String> {

    TradeRecommendationEntity findByTicker(String ticker);

    @Transactional
    void deleteByTicker(String ticker);

    boolean existsByTicker(String ticker);


}
