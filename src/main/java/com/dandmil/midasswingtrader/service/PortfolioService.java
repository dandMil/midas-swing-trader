package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.entity.PortfolioEntity;
import com.dandmil.midasswingtrader.pojo.PortfolioEntry;
import com.dandmil.midasswingtrader.pojo.TradeRecommendation;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.polygon.Result;
import com.dandmil.midasswingtrader.repository.PortfolioRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.dandmil.midasswingtrader.constants.Constants.FETCH_HISTORY;

@Service
public class PortfolioService {


    private final PortfolioRepository portfolioRepository;

    private final TradeRecommendationService tradeRecommendationService;

    private final PolygonAdapter polygonAdapter;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository,
                            TradeRecommendationService tradeRecommendationService,
                            PolygonAdapter polygonAdapter) {
        this.portfolioRepository = portfolioRepository;
        this.tradeRecommendationService = tradeRecommendationService;
        this.polygonAdapter = polygonAdapter;
    }


    public ResponseEntity<String> purchaseAssets(String ticker, int shares, double price) {

        PortfolioEntity portfolioEntity = new PortfolioEntity();

        double newPrice = price;
        int newShares = shares;
        if (portfolioRepository.existsByName(ticker)) {
            PortfolioEntity oldEntry = portfolioRepository.findByName(ticker);
            double oldPrice = oldEntry.getPrice();
            int oldShares = oldEntry.getShares();
            newPrice = calculateDollarCostAverage(oldPrice,price,oldShares,shares);
            newShares = shares+oldShares;
            portfolioRepository.deleteByName(ticker);
        }
            portfolioEntity.setType("stock");
            portfolioEntity.setPrice(newPrice);
            portfolioEntity.setShares(newShares);
            portfolioEntity.setName(ticker);
            portfolioRepository.save(portfolioEntity);
           TradeRecommendation tradeRecommendation = tradeRecommendationService.calculateTradeRecommendations(ticker,newPrice);

            tradeRecommendationService.saveTradeRecommendation(tradeRecommendation,ticker,newShares);

        return null;
    }

    public List<PortfolioEntry> fetchPortfolio() {
        List<PortfolioEntity> entities = portfolioRepository.findAll();
        List<PortfolioEntry> portfolioList = new ArrayList<>();

        if (!entities.isEmpty()) {
            for (PortfolioEntity portfolioEntity : entities) {
                PortfolioEntry entry = new PortfolioEntry();

                String ticker = portfolioEntity.getName();
                Mono<ApiResponse> apiResponseMono = polygonAdapter.makeApiCall(ticker, FETCH_HISTORY);
                PolygonResponse response = (PolygonResponse) apiResponseMono.block();
                if (!response.getResults().isEmpty()) {
                    Result result = response.getResults().get(response.getResults().size() - 1);
                    double lastPrice = result.getC();
                    TradeRecommendation tradeRecommendation = tradeRecommendationService.fetchTradeRecommendation(ticker);
                    entry.setShares(portfolioEntity.getShares());
                    entry.setTicker(portfolioEntity.getName());
                    entry.setTradeRecommendation(tradeRecommendation);
                    entry.setCurrentPrice(lastPrice);

                    portfolioList.add(entry);
                }
            }
        }
            return portfolioList;

        }

    private  double calculateDollarCostAverage(double oldPrice, double newPrice, int oldShares, int newShares) {
        // Total investment in the old shares
        double oldInvestment = oldPrice * oldShares;

        // Total investment in the new shares
        double newInvestment = newPrice * newShares;

        // Total number of shares after the new purchase
        int totalShares = oldShares + newShares;

        // Total investment after the new purchase
        double totalInvestment = oldInvestment + newInvestment;

        // Calculate the dollar cost average
        return totalInvestment / totalShares;
    }

    }
