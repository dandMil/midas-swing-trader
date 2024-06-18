package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.entity.PortfolioEntity;
import com.dandmil.midasswingtrader.pojo.PortfolioEntry;
import com.dandmil.midasswingtrader.pojo.TradeRecommendation;
import com.dandmil.midasswingtrader.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {


    @Autowired
    PortfolioRepository portfolioRepository;


    @Autowired
    TradeRecommendationService tradeRecommendationService;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, TradeRecommendationService tradeRecommendationService) {
            this.portfolioRepository = portfolioRepository;
            this.tradeRecommendationService = tradeRecommendationService;
    }


    public ResponseEntity<String> purchaseAssets(String ticker, int shares, double price) {

        PortfolioEntity portfolioEntity = new PortfolioEntity();
        portfolioEntity.setType("stock");
        portfolioEntity.setPrice(price);
        portfolioEntity.setShares(shares);
        portfolioEntity.setName(ticker);
        if (portfolioRepository.existsByName(ticker)){
            portfolioRepository.deleteByName(ticker);
        }
            portfolioRepository.save(portfolioEntity);
           TradeRecommendation tradeRecommendation = tradeRecommendationService.calculateTradeRecommendations(ticker,price);
            tradeRecommendationService.saveTradeRecommendation(tradeRecommendation,ticker,shares);

        return null;
    }

    public List<PortfolioEntry> fetchPortfolio() {
        List<PortfolioEntity> entities = portfolioRepository.findAll();
        List<PortfolioEntry> portfolioList = new ArrayList<>();
        if (!entities.isEmpty()) {
            for (PortfolioEntity portfolioEntity: entities){
                PortfolioEntry entry = new PortfolioEntry();

                String ticker = portfolioEntity.getName();
                TradeRecommendation tradeRecommendation = tradeRecommendationService.fetchTradeRecommendation(ticker);
                entry.setShares(portfolioEntity.getShares());
                entry.setTicker(portfolioEntity.getName());
                entry.setTradeRecommendation(tradeRecommendation);


                portfolioList.add(entry);
            }
        }
        return portfolioList;


    }
}
