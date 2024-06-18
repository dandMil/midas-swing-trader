package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.TIUtils;
import com.dandmil.midasswingtrader.controller.MidasController;
import com.dandmil.midasswingtrader.entity.TradeRecommendationEntity;
import com.dandmil.midasswingtrader.repository.*;
import com.dandmil.midasswingtrader.pojo.AssetSignalIndicator;
import com.dandmil.midasswingtrader.pojo.TradeRecommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.dandmil.midasswingtrader.constants.Constants.*;

@Service
public class TradeRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(TradeRecommendationService.class);

    //TODO make this scale with volatility
    private static final double PROFIT_TARGET_PERCENTAGE = 10.0;
    private static final double STOP_LOSS_PERCENTAGE = 5.0;

    private static final double HIGH_VOLATILITY_THRESHOLD = 1.5; // Example threshold for high volatility
    private static final double LOW_VOLATILITY_THRESHOLD = 0.5; // Example threshold for low volatility

    private static final double PROFIT_TARGET_MULTIPLIER = 3.0; // 3x ATR for profit target
    private static final double STOP_LOSS_MULTIPLIER = 2.0; // 2x ATR for stop loss

    private final TechnicalIndicatorService technicalIndicatorService;

    private final TradeRecommendationRepository tradeRecommendationRepository;

    @Autowired
    public TradeRecommendationService(TechnicalIndicatorService technicalIndicatorService, TradeRecommendationRepository tradeRecommendationRepository){
            this.technicalIndicatorService = technicalIndicatorService;
        this.tradeRecommendationRepository = tradeRecommendationRepository;
    }

    public TradeRecommendation calculateTradeRecommendations(String ticker, double entryPrice) {
        // Fetch technical indicators
        AssetSignalIndicator response = technicalIndicatorService.calculateTechnicalIndicators(ticker, "stock");
        TradeRecommendation tradeRecommendation = new TradeRecommendation();

        if (response != null) {
            // Select strategy based on technical indicators
            String strategy = selectStrategy(response.getAtr(), response.getStochasticOscillator(),
                    response.getRelativeStrengthIndex(), response.getMacd(), response.getPriceRateOfChange());

            tradeRecommendation.setStrategy(strategy);
            tradeRecommendation.setPriceEntry(TIUtils.roundTo3SigFigs(entryPrice));
            tradeRecommendation.setRecommendationDate(new Date());
            double profitTarget = 0.0;
            double stopLoss = 0.0;
            if (PERCENTAGE_BASED.equalsIgnoreCase(strategy)) {
                // Percentage-Based Strategy
                 profitTarget = entryPrice * (1 + PROFIT_TARGET_PERCENTAGE / 100);
                 stopLoss = entryPrice * (1 - STOP_LOSS_PERCENTAGE / 100);
                tradeRecommendation.setTakeProfit(TIUtils.roundTo3SigFigs(profitTarget));
                tradeRecommendation.setStopLoss(TIUtils.roundTo3SigFigs(stopLoss));
            } else if (VOLATILITY_BASED.equalsIgnoreCase(strategy)) {
                // Volatility-Based Strategy
                double atr = response.getAtr();
                 profitTarget = entryPrice + (PROFIT_TARGET_MULTIPLIER * atr);
                 stopLoss = entryPrice - (STOP_LOSS_MULTIPLIER * atr);
                tradeRecommendation.setTakeProfit(TIUtils.roundTo3SigFigs(profitTarget));
                tradeRecommendation.setStopLoss(TIUtils.roundTo3SigFigs(stopLoss));
            }
            double expectedProfit = TIUtils.roundTo3SigFigs(profitTarget - entryPrice);
            double expectedLoss = TIUtils.roundTo3SigFigs(entryPrice - stopLoss);

            tradeRecommendation.setExpectedProfit(expectedProfit);
            tradeRecommendation.setExpectedLoss(expectedLoss);
        }

        return tradeRecommendation;
    }

    private String selectStrategy(double atr, double so, double rsi, double macd, double roc) {
        boolean isVolatilityHigh = atr > HIGH_VOLATILITY_THRESHOLD;
        boolean isMomentumStrong = Math.abs(macd) > 1.0 || Math.abs(roc) > 10.0;
        boolean isOverboughtOversold = rsi > 70 || rsi < 30 || so > 80 || so < 20;

        if (isVolatilityHigh || isMomentumStrong || isOverboughtOversold) {
            return VOLATILITY_BASED;
        } else {
            return PERCENTAGE_BASED;
        }
    }

    public TradeRecommendation fetchTradeRecommendation(String ticker){
        TradeRecommendationEntity tradeRecommendationEntity = tradeRecommendationRepository.findByTicker(ticker);
        TradeRecommendation tradeRecommendation = new TradeRecommendation();
        if (tradeRecommendationEntity !=null){
            tradeRecommendation.setStrategy(tradeRecommendationEntity.getStrategy());
            tradeRecommendation.setStopLoss(tradeRecommendationEntity.getStopLoss());
            tradeRecommendation.setTakeProfit(tradeRecommendationEntity.getTakeProfit());
            tradeRecommendation.setPriceEntry(tradeRecommendationEntity.getEntryPrice());
            tradeRecommendation.setExpectedLoss(tradeRecommendationEntity.getExpectedLoss());
            tradeRecommendation.setExpectedProfit(tradeRecommendationEntity.getExpectedProfit());
            tradeRecommendation.setPriceEntry(tradeRecommendationEntity.getEntryPrice());
        }

    return tradeRecommendation;
    }

    public void saveTradeRecommendation(TradeRecommendation tradeRecommendation,String ticker,int shares){
        TradeRecommendationEntity entity = new TradeRecommendationEntity();
        logger.info("Saving to Trade Recommendation");

            if(tradeRecommendationRepository.existsByTicker(ticker)){
                tradeRecommendationRepository.deleteByTicker(ticker);
            }
            entity.setEntryPrice(tradeRecommendation.getPriceEntry());
            entity.setExpectedProfit(tradeRecommendation.getExpectedProfit()*shares);
            entity.setStrategy(tradeRecommendation.getStrategy());
            entity.setExpectedLoss(tradeRecommendation.getExpectedLoss()*shares);
            entity.setStopLoss(tradeRecommendation.getStopLoss());
            entity.setTakeProfit(tradeRecommendation.getTakeProfit());
            entity.setRecommendationDate(tradeRecommendation.getRecommendationDate());
            entity.setTicker(ticker);
            tradeRecommendationRepository.save(entity);

    }
}
