package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.entity.PortfolioEntity;
import com.dandmil.midasswingtrader.pojo.TradeRecommendation;
import com.dandmil.midasswingtrader.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {


    @Autowired
    PortfolioRepository portfolioRepository;


    @Autowired
    public PortfolioService(){

    }


    public ResponseEntity<String> purchaseAssets(String ticker, int shares, double price){

        PortfolioEntity portfolioEntity = new PortfolioEntity();
        portfolioEntity.setType("stock");
        portfolioEntity.setPrice(price);
        portfolioEntity.setShares(shares);
        portfolioEntity.setName(ticker);

        List<PortfolioEntity> entities = portfolioRepository.findAllByName(ticker);
        if (entities.isEmpty()){
            portfolioRepository.save(portfolioEntity);
        }
        return null;
    }

//    public List<TradeRecommendation> fetchPortfolio(){
//
//
//    }

}
